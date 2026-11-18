import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  CommonFacilityTargetCompositionComponent,
  FacilityTargetCompositionSubmitEvent,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isCCA3FacilityWizardCompleted,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  updateFacilityTargetComposition,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';

@Component({
  selector: 'cca-target-composition',
  template: `<cca-common-facility-target-composition (submitted)="onSubmit($event)" />`,
  imports: [CommonFacilityTargetCompositionComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSubmit(event: FacilityTargetCompositionSubmitEvent) {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = updateFacilityTargetComposition(
      actionPayload,
      event.form,
      event.facilityId,
    ) as UnderlyingAgreementVariationReviewSavePayload;

    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === event.facilityId,
    );

    const currentFacility = updatedPayload.facilities?.find((f) => f.facilityId === event.facilityId);

    let areIdentical = false;

    if (originalFacility) {
      const resetOriginal = resetFacilityNonComparisonFields(originalFacility);
      const resetCurrent = resetFacilityNonComparisonFields(currentFacility);

      const filterOriginal = filterFieldsWithFalsyValues(resetOriginal);
      const filterCurrent = filterFieldsWithFalsyValues(resetCurrent);

      areIdentical = areEntitiesIdentical(filterCurrent, filterOriginal);
    }

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions)();
    const decisions = areIdentical ? deleteFacilityDecision(currentDecisions, event.facilityId) : currentDecisions;

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[event.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[event.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UNAVariationReviewRequestTaskPayload) => {
        const facility = updatedRequestTaskPayload.underlyingAgreement.facilities.find(
          (f) => f.facilityId === event.facilityId,
        );
        if (isCCA3FacilityWizardCompleted(facility)) {
          const targetPath = areIdentical ? '../check-your-answers' : '../decision';
          this.router.navigate([targetPath], { relativeTo: this.route });
        } else {
          this.router.navigate(['../', FacilityWizardStep.BASELINE_DATA], { relativeTo: this.route });
        }
      });
  }
}
