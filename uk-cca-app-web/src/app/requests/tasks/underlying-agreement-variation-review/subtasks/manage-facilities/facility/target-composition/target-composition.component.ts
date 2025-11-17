import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  FacilityTargetCompositionFormModel,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isCCA3FacilityWizardCompleted,
  MeasurementTypeEnum,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  transformMeasurementType,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import {
  FileInputComponent,
  TextInputComponent as CcaTextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { TargetComposition, UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';
import { TARGET_COMPOSITION_FORM, TargetCompositionFormProvider } from './target-composition-form.provider';

@Component({
  selector: 'cca-target-composition',
  templateUrl: './target-composition.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    SelectComponent,
    CcaTextInputComponent,
    RouterLink,
  ],
  providers: [TargetCompositionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly transformMeasurementType = transformMeasurementType;

  protected getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly measurementTypeOptions: GovukSelectOption<TargetComposition['measurementType']>[] = [
    {
      value: 'ENERGY_KWH',
      text: MeasurementTypeEnum.ENERGY_KWH,
    },
    {
      value: 'ENERGY_MWH',
      text: MeasurementTypeEnum.ENERGY_MWH,
    },
    {
      value: 'ENERGY_GJ',
      text: MeasurementTypeEnum.ENERGY_GJ,
    },
    {
      value: 'CARBON_KG',
      text: MeasurementTypeEnum.CARBON_KG,
    },
    {
      value: 'CARBON_TONNE',
      text: MeasurementTypeEnum.CARBON_TONNE,
    },
  ];

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateFacilityTargetComposition(actionPayload, this.form, this.facilityId);

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

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
      if (isCCA3FacilityWizardCompleted(facility)) {
        const targetPath = areIdentical ? '../check-your-answers' : '../decision';
        this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../', FacilityWizardStep.BASELINE_DATA], { relativeTo: this.activatedRoute });
      }
    });
  }
}

function updateFacilityTargetComposition(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FacilityTargetCompositionFormModel,
  facilityId: string,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    if (draft.facilities[facilityIndex]?.cca3BaselineAndTargets?.targetComposition) {
      draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition,
        ...form.value,
        calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
      };
    } else {
      draft.facilities[facilityIndex].cca3BaselineAndTargets = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets,
        targetComposition: {
          calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
          measurementType: form.value.measurementType,
          agreementCompositionType: form.value.agreementCompositionType ?? 'NOVEM',
        },
      };
    }
  });
}
