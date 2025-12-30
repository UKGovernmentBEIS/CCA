import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  FacilityBaselineDataFormModel,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isFacilityWizardCompleted,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';
import { FACILITY_BASELINE_DATA_FORM, FacilityBaselineDataFormProvider } from './baseline-data-form.provider';

@Component({
  selector: 'cca-baseline-data',
  templateUrl: './baseline-data.component.html',
  imports: [
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    DateInputComponent,
    TextareaComponent,
    TextInputComponent,
    MultipleFileInputComponent,
    RouterLink,
    WizardStepComponent,
  ],
  providers: [FacilityBaselineDataFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineDataComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityBaselineDataFormModel>(FACILITY_BASELINE_DATA_FORM);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly una = this.store.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
  private readonly facilityIndex = this.una.facilities?.findIndex((f) => f.facilityId === this.facilityId) ?? -1;

  protected readonly downloadUrl = generateDownloadUrl(this.taskId.toString());

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly isTwelveMonthsValue = toSignal(this.form.controls.isTwelveMonths.valueChanges, {
    initialValue: this.form.controls.isTwelveMonths.value,
  });

  protected readonly twelveMonthsSelected = computed(() => typeof this.isTwelveMonthsValue() === 'boolean');

  protected readonly baselineDateValue = toSignal(this.form.controls.baselineDate.valueChanges, {
    initialValue: this.form.value.baselineDate,
  });

  protected readonly dateIsGreaterThanStartOf2022 = computed(
    () => this.baselineDateValue() && this.baselineDateValue().getTime() > new Date('2022-01-01').getTime(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = update(actionPayload, this.form, this.facilityId, this.dateIsGreaterThanStartOf2022());

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

    const currDetermination = this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
      if (isFacilityWizardCompleted(facility)) {
        const targetPath = areIdentical ? '../check-your-answers' : '../decision';
        this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../', FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION], {
          relativeTo: this.activatedRoute,
        });
      }
    });
  }
}

function update(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FacilityBaselineDataFormModel,
  facilityId: string,
  dateIsGreaterThanStartOf2022: boolean,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const baselineDate = form.controls.baselineDate?.value;
    const greenfieldEvidences = fileUtils.toUUIDs(form.controls.greenfieldEvidences?.value);
    const isTwelveMonths = form.controls.isTwelveMonths?.value;

    draft.facilities[facilityIndex].cca3BaselineAndTargets.baselineData = {
      isTwelveMonths,
      baselineDate: baselineDate ? baselineDate.toISOString().split('T')[0] : null, // Format as YYYY-MM-DD
      explanation: !isTwelveMonths || dateIsGreaterThanStartOf2022 ? form.controls.explanation?.value : null,
      greenfieldEvidences: Array.isArray(greenfieldEvidences) && !isTwelveMonths ? greenfieldEvidences : [],
      usedReportingMechanism: form.controls.usedReportingMechanism?.value,
      energyCarbonFactor: String(form.controls.energyCarbonFactor?.value),
    };
  });
}
