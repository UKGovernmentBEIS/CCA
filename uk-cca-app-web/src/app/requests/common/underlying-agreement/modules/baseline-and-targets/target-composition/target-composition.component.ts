import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { startWith } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import {
  ConditionalContentDirective,
  DetailsComponent,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { FileInputComponent, MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { TextInputComponent as CcaTextInputComponent } from '@shared/components/text-input/text-input.component';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { TargetComposition } from 'cca-api';

import {
  AgreementCompositionTypeEnum,
  AgreementCompositionTypePipe,
  ApplicationReasonTypePipe,
  MeasurementTypeEnum,
  MeasurementTypeToOptionTextPipe,
  transformMeasurementType,
} from '../../../pipes';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
} from '../../../underlying-agreement.types';
import {
  TARGET_COMPOSITION_FORM,
  TargetCompositionFormModel,
  TargetCompositionFormProvider,
} from './target-composition-form.provider';

@Component({
  selector: 'cca-target-composition',
  standalone: true,
  imports: [
    FormsModule,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    TextInputComponent,
    MeasurementTypeToOptionTextPipe,
    SelectComponent,
    ApplicationReasonTypePipe,
    AgreementCompositionTypePipe,
    DetailsComponent,
    MultipleFileInputComponent,
    CcaTextInputComponent,
    ConditionalContentDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './target-composition.component.html',
  providers: [TargetCompositionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<TargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly isTargetPeriod5 =
    this.baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  readonly agreementCompositionTypeValue = toSignal(
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
    this.taskService
      .saveSubtask(
        this.isTargetPeriod5
          ? BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
          : BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
        BaseLineAndTargetsStep.TARGET_COMPOSITION,
        this.activatedRoute,
        this.form.value,
      )
      .subscribe();
  }
}
