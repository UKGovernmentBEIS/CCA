import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  VariationFacilityDetailsFormComponent,
  VariationFacilityDetailsFormModel,
  VariationFacilityDetailsFormProvider,
} from '@requests/common';
import { produce } from 'immer';

import {
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';

@Component({
  selector: 'cca-facility-details',
  template: `<cca-variation-facility-details-form (submitChange)="onSubmit($event)" />`,
  standalone: true,
  imports: [VariationFacilityDetailsFormComponent],
  providers: [VariationFacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit(form: FormGroup<VariationFacilityDetailsFormModel>) {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Update or add facility in the facilities array
    const updatedPayload = this.update(actionPayload, form, this.facilityId);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationSubmitRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        if (isFacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate([`../${FacilityWizardStep.CONTACT_DETAILS}`], { relativeTo: this.activatedRoute });
        }
      });
  }

  private update(
    payload: UnderlyingAgreementVariationApplySavePayload,
    form: FormGroup<VariationFacilityDetailsFormModel>,
    facilityId: string,
  ): UnderlyingAgreementVariationApplySavePayload {
    return produce(payload, (draft) => {
      const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
      if (facilityIndex === -1) return;

      draft.facilities[facilityIndex].facilityDetails = {
        ...draft.facilities[facilityIndex].facilityDetails,
        name: form.controls.name.value,
        isCoveredByUkets: form.value.isCoveredByUkets,
        uketsId: form.value.uketsId,
        applicationReason: form.getRawValue().applicationReason,
        previousFacilityId: form.getRawValue().previousFacilityId,
        facilityAddress: form.getRawValue().facilityAddress,
      };
    });
  }
}
