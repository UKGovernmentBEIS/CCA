import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable, switchMap, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  FacilityDetailsFormComponent,
  FacilityDetailsFormModel,
  FacilityDetailsFormProvider,
  FacilityWizardStep,
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { produce } from 'immer';

import {
  FacilityDetails,
  FacilityItem,
  FacilityService,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-add-facility',
  template: `<cca-facility-details-form (submitChange)="onSubmit($event)" />`,
  standalone: true,
  imports: [FacilityDetailsFormComponent],
  providers: [FacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddFacilityComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly facilityService = inject(FacilityService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly requestInfo = this.store.select(requestTaskQuery.selectRequestInfo);

  onSubmit(form: FormGroup<FacilityDetailsFormModel>) {
    this.facilityService
      .generateFacilityId(this.requestInfo().accountId)
      .pipe(switchMap((facility) => this.update(form, facility.facilityId)))
      .subscribe();
  }

  private update(form: FormGroup<FacilityDetailsFormModel>, facilityId: string): Observable<unknown> {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateFacility(actionPayload, form, facilityId);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    return this.tasksApiService.saveRequestTaskAction(dto).pipe(
      tap(() => {
        this.router.navigate(['../', facilityId, FacilityWizardStep.CONTACT_DETAILS], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      }),
    );
  }
}

function updateFacility(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const participatingSchemeVersions = form.value.participatingSchemeVersions;

    const facilityDetails: FacilityDetails = {
      name: form.value.name,
      participatingSchemeVersions: participatingSchemeVersions,
      isCoveredByUkets: form.value.isCoveredByUkets ?? false,
      applicationReason: form.value.applicationReason,
      facilityAddress: form.getRawValue().facilityAddress,
      ...(form.value.uketsId && { uketsId: form.value.uketsId }),
      ...(form.value.previousFacilityId && { previousFacilityId: form.value.previousFacilityId }),
    };

    const facilityItem: FacilityItem = {
      facilityId,
      facilityDetails,
      facilityContact: null,
      eligibilityDetailsAndAuthorisation: null,
      facilityExtent: null,
      apply70Rule: null,
      cca3BaselineAndTargets: isCCA3Scheme(participatingSchemeVersions)
        ? {
            baselineData: null,
            facilityTargets: null,
            targetComposition: null,
          }
        : null,
    };

    if (!draft.facilities) draft.facilities = [];

    draft.facilities.push(facilityItem);
  });
}
