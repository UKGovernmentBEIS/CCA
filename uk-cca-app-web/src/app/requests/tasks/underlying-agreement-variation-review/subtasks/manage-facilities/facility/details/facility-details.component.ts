import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  applySchemeVersionsSideEffect,
  areEntitiesIdentical,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isCCA2FacilityWizardCompleted,
  isCCA3FacilityWizardCompleted,
  isCCA3Scheme,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  VariationFacilityDetailsFormComponent,
  VariationFacilityDetailsFormModel,
  VariationFacilityDetailsFormProvider,
} from '@requests/common';
import { produce } from 'immer';

import { Facility, UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';

@Component({
  selector: 'cca-facility-details',
  template: `<cca-variation-facility-details-form (submitChange)="onSubmit($event)" />`,
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
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    let updatedPayload = updateFacilityDetails(actionPayload, form, this.facilityId);

    updatedPayload = applySchemeVersionsSideEffect(
      updatedPayload,
      this.facilityId,
    ) as UnderlyingAgreementVariationReviewSavePayload;

    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === this.facilityId,
    );
    const currentFacility = updatedPayload.facilities?.find((f) => f.facilityId === this.facilityId);

    let areIdentical = false;

    if (originalFacility) {
      const resetOriginal = resetFacilityNonComparisonFields(originalFacility);
      const resetCurrent = resetFacilityNonComparisonFields(currentFacility);

      const filterOriginal = filterFieldsWithFalsyValues(resetOriginal);
      const filterCurrent = filterFieldsWithFalsyValues(resetCurrent);

      areIdentical = areEntitiesIdentical(filterCurrent, filterOriginal);
    }

    const currentDecisions = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
    )();
    const decisions = areIdentical ? deleteFacilityDecision(currentDecisions, this.facilityId) : currentDecisions;

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const reviewSectionsCompleted = produce(payload.reviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const determination = resetDetermination(payload.determination);
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
      this.router.navigate(nextRoute(facility, areIdentical), { relativeTo: this.activatedRoute });
    });
  }
}

function updateFacilityDetails(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FormGroup<VariationFacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
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

function nextRoute(facility: Facility, areIdentical: boolean): string[] {
  const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions;
  const targetPath = areIdentical ? '../check-your-answers' : '../decision';

  if (isCCA3Scheme(schemeVersions)) {
    if (isCCA3FacilityWizardCompleted(facility)) return [targetPath];

    return isCCA2FacilityWizardCompleted(facility)
      ? ['../', FacilityWizardStep.TARGET_COMPOSITION]
      : ['../', FacilityWizardStep.CONTACT_DETAILS];
  }

  return isCCA2FacilityWizardCompleted(facility) ? [targetPath] : ['../', FacilityWizardStep.CONTACT_DETAILS];
}
