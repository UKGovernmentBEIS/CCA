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
  TaskItemStatus,
  TasksApiService,
  transformMeasurementType,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import {
  FileInputComponent,
  MultipleFileInputComponent,
  TextInputComponent as CcaTextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { TargetComposition } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import {
  TARGET_COMPOSITION_FORM,
  TargetCompositionFormModel,
  TargetCompositionFormProvider,
} from './target-composition-form.provider';

@Component({
  selector: 'cca-target-composition',
  templateUrl: './target-composition.component.html',
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
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<TargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  private readonly taskId = this.route.snapshot.paramMap.get('taskId');

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
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    const isTargetUnitThroughputMeasured = this.form.value.isTargetUnitThroughputMeasured;
    const sectorThroughputUnit = this.form.controls.sectorAssociationThroughputUnit?.value;

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod6Details.targetComposition = {
        calculatorFile: this.form.value.calculatorFile?.uuid,
        measurementType: this.form.value.measurementType,
        agreementCompositionType: this.form.value.agreementCompositionType,
        isTargetUnitThroughputMeasured,
        throughputUnit:
          isTargetUnitThroughputMeasured === false && !!sectorThroughputUnit
            ? sectorThroughputUnit
            : this.form.controls.throughputUnit.value,
        conversionFactor: this.form.value.conversionFactor,
        conversionEvidences: fileUtils.toUUIDs(this.form.value.conversionEvidences) as string[],
      };
    });

    const finalPayload = applyTp6TargetCompositionSideEffect(updatedPayload);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, finalPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

      const targetPeriodDetails = this.requestTaskStore.select(
        underlyingAgreementQuery.selectTargetPeriodDetails(false),
      )();

      const completed = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);

      if (completed) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
      } else {
        this.router.navigate([`../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`], { relativeTo: this.route });
      }
    });
  }
}
