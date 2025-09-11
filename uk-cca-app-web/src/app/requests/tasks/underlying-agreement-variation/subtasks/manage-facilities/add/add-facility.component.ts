import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
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
  standalone: true,
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
      .generateFacilityId(this.requestInfo().accountId)
      .pipe(switchMap((facility) => this.update(form, facility.facilityId)))
      .subscribe();
  }

  private update(form: FormGroup<VariationFacilityDetailsFormModel>, facilityId: string) {
    // Step 1: Get payload from store
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    // Step 2: Transform to save action payload
    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Step 3: Apply business logic transformations
    const updatedPayload = updateFacility(savePayload, form, facilityId);

    // Update sections completed
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    // Create and send DTO
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedProps = resetFacilityReviewSection(reviewProps, facilityId);

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
    const facilityDetails: FacilityDetails = {
      name: form.value.name,
      isCoveredByUkets: form.value.isCoveredByUkets ?? false,
      applicationReason: form.value.applicationReason,
      facilityAddress: form.getRawValue().facilityAddress,
      ...(form.value.uketsId && { uketsId: form.value.uketsId }),
      ...(form.value.previousFacilityId && { previousFacilityId: form.value.previousFacilityId }),
      participatingSchemeVersions: ['CCA_2'],
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
    };

    if (!draft.facilities) draft.facilities = [];

    draft.facilities.push(facilityItem);
  });
}
