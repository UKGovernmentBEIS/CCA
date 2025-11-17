import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  VariationFacilityDetailsFormComponent,
  VariationFacilityDetailsFormModel,
  VariationFacilityDetailsFormProvider,
} from '@requests/common';
import { produce } from 'immer';

import {
  Facility,
  FacilityDetails,
  FacilityService,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, resetFacilityReviewSection } from '../../../utils';

@Component({
  selector: 'cca-add-facility',
  template: `<cca-variation-facility-details-form (submitChange)="onSubmit($event)" />`,
  imports: [VariationFacilityDetailsFormComponent],
  providers: [VariationFacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddFacilityComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly facilityService = inject(FacilityService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly requestInfo = this.requestTaskStore.select(requestTaskQuery.selectRequestInfo);

  onSubmit(form: FormGroup<VariationFacilityDetailsFormModel>) {
    this.facilityService
      .generateFacilityBusinessId(this.requestInfo().accountId)
      .pipe(switchMap((facility) => this.update(form, facility.facilityBusinessId)))
      .subscribe();
  }

  private update(form: FormGroup<VariationFacilityDetailsFormModel>, facilityId: string) {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

    const updatedPayload = updateFacility(savePayload, form, facilityId);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);

    // The third argument (false) is for stating if the current facility is identical to the original.
    // Since here this is a new facility, we explicitly set it to false.
    const resetedProps = resetFacilityReviewSection(reviewProps, facilityId, false);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedProps,
    });

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
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<VariationFacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    const participatingSchemeVersions = form.value.participatingSchemeVersions;

    const facilityDetails: FacilityDetails = {
      name: form.value.name,
      participatingSchemeVersions,
      isCoveredByUkets: form.value.isCoveredByUkets ?? false,
      applicationReason: form.value.applicationReason,
      facilityAddress: form.getRawValue().facilityAddress,
      ...(form.value.uketsId && { uketsId: form.value.uketsId }),
      ...(form.value.previousFacilityId && { previousFacilityId: form.value.previousFacilityId }),
    };

    const facilityItem: Facility = {
      facilityId,
      facilityDetails,
      facilityContact: null,
      eligibilityDetailsAndAuthorisation: null,
      facilityExtent: null,
      apply70Rule: null,
      status: 'NEW',
      excludedDate: null,
      cca3BaselineAndTargets: isCCA3Scheme(participatingSchemeVersions)
        ? {
            baselineData: null,
            facilityTargets: null,
            targetComposition: null,
            facilityBaselineEnergyConsumption: null,
          }
        : null,
    };

    if (!draft.facilities) draft.facilities = [];

    draft.facilities.push(facilityItem);
  });
}
