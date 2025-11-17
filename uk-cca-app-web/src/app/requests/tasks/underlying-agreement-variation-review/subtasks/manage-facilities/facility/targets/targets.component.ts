import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { Improvement } from '@shared/types';
import { produce } from 'immer';

import { FacilityTargets, UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';
import { FACILITY_TARGETS_FORM, FacilityTargetsFormModel, FacilityTargetsFormProvider } from './targets-form.provider';

@Component({
  selector: 'cca-targets',
  templateUrl: './targets.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, TextInputComponent, RouterLink],
  providers: [FacilityTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetsFormModel>(FACILITY_TARGETS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateFacilityTargets(actionPayload, this.form, this.facilityId);

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

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions)();
    const decisions = areIdentical ? deleteFacilityDecision(currentDecisions, this.facilityId) : currentDecisions;

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const targetPath = areIdentical ? '../check-your-answers' : '../decision';
      this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
    });
  }
}

function updateFacilityTargets(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FacilityTargetsFormModel,
  facilityId: string,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const improvements: FacilityTargets['improvements'] = {
      [Improvement.TP7]: String(form.value.tp7),
      [Improvement.TP8]: String(form.value.tp8),
      [Improvement.TP9]: String(form.value.tp9),
    };

    draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityTargets = {
      improvements,
    };
  });
}
