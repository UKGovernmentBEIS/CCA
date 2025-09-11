import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  applySchemeVersionsSideEffect,
  FACILITY_DETAILS_FORM,
  FacilityDetailsFormComponent,
  FacilityDetailsFormModel,
  FacilityDetailsFormProvider,
  FacilityWizardStep,
  isCCA2FacilityWizardCompleted,
  isCCA3FacilityWizardCompleted,
  isCCA3Scheme,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { produce } from 'immer';

import { Facility, UnderlyingAgreementApplySavePayload, UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';

@Component({
  selector: 'cca-facility-details',
  template: `<cca-facility-details-form (submitChange)="onSubmit($event)" />`,
  standalone: true,
  imports: [FacilityDetailsFormComponent],
  providers: [FacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityDetailsFormModel>>(FACILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  onSubmit(form: FormGroup<FacilityDetailsFormModel>) {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);

    let updatedPayload = updateFacilityDetails(actionPayload, form, this.facilityId);
    updatedPayload = applySchemeVersionsSideEffect(updatedPayload, this.facilityId);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    // Create DTO and make API call
    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted: this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        this.router.navigate(nextRoute(facility), { relativeTo: this.activatedRoute });
      });
  }
}

function updateFacilityDetails(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const participatingSchemeVersions = form.value.participatingSchemeVersions;

    draft.facilities[facilityIndex].facilityDetails = {
      ...draft.facilities[facilityIndex].facilityDetails,
      name: form.controls.name.value,
      participatingSchemeVersions,
      isCoveredByUkets: form.value.isCoveredByUkets,
      uketsId: form.value.uketsId,
      applicationReason: form.getRawValue().applicationReason,
      previousFacilityId: form.getRawValue().previousFacilityId,
      facilityAddress: form.getRawValue().facilityAddress,
    };
  });
}

function nextRoute(facility: Facility): string[] {
  const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions;

  if (isCCA3Scheme(schemeVersions)) {
    if (isCCA3FacilityWizardCompleted(facility)) return ['../decision'];

    return isCCA2FacilityWizardCompleted(facility)
      ? ['../', FacilityWizardStep.TARGET_COMPOSITION]
      : ['../', FacilityWizardStep.CONTACT_DETAILS];
  }

  return isCCA2FacilityWizardCompleted(facility) ? ['../decision'] : ['../', FacilityWizardStep.CONTACT_DETAILS];
}
