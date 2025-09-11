import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { startWith } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DetailsComponent,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  AgreementCompositionTypeEnum,
  AgreementCompositionTypePipe,
  applyTp6TargetCompositionSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  isTargetPeriodWizardCompleted,
  MeasurementTypeEnum,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  transformMeasurementType,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import {
  FileInputComponent,
  MultipleFileInputComponent,
  TextInputComponent as CcaTextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { TargetComposition } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  TARGET_COMPOSITION_FORM,
  TargetCompositionFormModel,
  TargetCompositionFormProvider,
} from './target-composition-form.provider';

@Component({
  selector: 'cca-target-composition',
  templateUrl: './target-composition.component.html',
  standalone: true,
  imports: [
    FormsModule,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    TextInputComponent,
    SelectComponent,
    AgreementCompositionTypePipe,
    DetailsComponent,
    MultipleFileInputComponent,
    CcaTextInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [TargetCompositionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly form = inject<TargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly agreementCompositionTypeValue = toSignal(
    this.form.controls.agreementCompositionType.valueChanges.pipe(
      startWith(this.form.controls.agreementCompositionType.value),
    ),
  );

  protected readonly targetUnitThroughputMeasuredDiffers = toSignal(
    this.form.controls.isTargetUnitThroughputMeasured.valueChanges,
    { initialValue: this.form.value.isTargetUnitThroughputMeasured },
  );

  protected readonly agreementCompositionTypes = Object.keys(AgreementCompositionTypeEnum) as Array<
    keyof typeof AgreementCompositionTypeEnum
  >;

  protected readonly sectorAssociationThroughputUnit = this.form.controls.sectorAssociationThroughputUnit.value;

  protected readonly isAgreementCompositionTypeNovem = computed(() => {
    return this.agreementCompositionTypeValue() === 'NOVEM';
  });

  protected readonly transformMeasurementType = transformMeasurementType;

  protected readonly showDifferentThroughputUnitControls = computed(() => {
    return !!this.sectorAssociationThroughputUnit && !!this.targetUnitThroughputMeasuredDiffers();
  });

  protected getSingleFileDownloadUrl(uuid: string) {
    return ['../../../file-download', uuid];
  }

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly measurementTypeOptions: GovukSelectOption<TargetComposition['measurementType']>[] = [
    {
      value: 'ENERGY_KWH',
      text: MeasurementTypeEnum['ENERGY_KWH'],
    },
    {
      value: 'ENERGY_MWH',
      text: MeasurementTypeEnum['ENERGY_MWH'],
    },
    {
      value: 'ENERGY_GJ',
      text: MeasurementTypeEnum['ENERGY_GJ'],
    },
    {
      value: 'CARBON_KG',
      text: MeasurementTypeEnum['CARBON_KG'],
    },
    {
      value: 'CARBON_TONNE',
      text: MeasurementTypeEnum['CARBON_TONNE'],
    },
  ];

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const isTargetUnitThroughputMeasured = this.form.controls.isTargetUnitThroughputMeasured.value;
    const sectorThroughputUnit = this.form.controls.sectorAssociationThroughputUnit?.value;

    // Direct form to payload mapping
    let updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod6Details.targetComposition = {
        calculatorFile: this.form.controls.calculatorFile.value?.uuid ?? null,
        measurementType: this.form.controls.measurementType.value,
        agreementCompositionType: this.form.controls.agreementCompositionType.value,
        isTargetUnitThroughputMeasured,
        throughputUnit:
          isTargetUnitThroughputMeasured === false && !!sectorThroughputUnit
            ? sectorThroughputUnit
            : this.form.controls.throughputUnit.value,
        conversionFactor: this.form.controls.conversionFactor.value,
        conversionEvidences: this.form.controls.conversionEvidences.value?.map((f) => f.uuid) || [],
      };
    });

    // Apply side effects separately
    updatedPayload = applyTp6TargetCompositionSideEffect(updatedPayload);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const wizardCompleted = isTargetPeriodWizardCompleted(payload.underlyingAgreement.targetPeriod6Details);

      wizardCompleted
        ? this.router.navigate(['../decision'], { relativeTo: this.activatedRoute })
        : this.router.navigate(['../', BaseLineAndTargetsStep.ADD_BASELINE_DATA], { relativeTo: this.activatedRoute });
    });
  }
}
