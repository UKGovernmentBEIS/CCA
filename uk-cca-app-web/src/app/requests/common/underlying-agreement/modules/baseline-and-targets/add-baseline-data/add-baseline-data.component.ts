import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { distinct } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';

import { underlyingAgreementQuery } from '../../../+state';
import { MeasurementTypeToOptionTextPipe, MeasurementTypeToUnitPipe } from '../../../pipes';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
} from '../../../underlying-agreement.types';
import { calculatePerformance, getMeasurementAndThroughputUnits } from '../baseline-and-targets.helper';
import {
  ADD_BASELINE_DATA_FORM,
  AddBaselineDataFormModel,
  AddBaselineDataFormProvider,
} from './add-baseline-data-form.provider';

@Component({
  selector: 'cca-add-baseline-data',
  standalone: true,
  imports: [
    WizardStepComponent,
    FormsModule,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    DateInputComponent,
    TextareaComponent,
    TextInputComponent,
    MultipleFileInputComponent,
    MeasurementTypeToOptionTextPipe,
    MeasurementTypeToUnitPipe,
    PendingButtonDirective,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './add-baseline-data.component.html',
  providers: [AddBaselineDataFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddBaselineDataComponent {
  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly isTargetPeriod5 = this.baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  readonly form = inject<AddBaselineDataFormModel>(ADD_BASELINE_DATA_FORM);

  readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(this.isTargetPeriod5),
  )();

  readonly sectorThroughputUnit = this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)()
    ?.sectorAssociationDetails?.throughputUnit;

  readonly isTwelveMonthsValue = toSignal(this.form.get('isTwelveMonths').valueChanges, {
    initialValue: this.form.get('isTwelveMonths').value,
  });
  readonly twelveMonthsSelected = computed(() => typeof this.isTwelveMonthsValue() === 'boolean');

  private readonly energyOrCarbonValue = toSignal(this.form.get('energy').valueChanges, {
    initialValue: this.form.get('energy').value,
  });

  private readonly throughputValue = toSignal(this.form.get('throughput').valueChanges, {
    initialValue: this.form.get('throughput').value,
  });
  readonly baselineDateValue = toSignal(this.form.controls.baselineDate.valueChanges, {
    initialValue: this.form.value.baselineDate,
  });
  readonly calculatedPerformance = computed(() => {
    if (this.targetComposition?.agreementCompositionType !== 'RELATIVE') return null;
    const energyOrCarbon = this.energyOrCarbonValue();
    const throughput = this.throughputValue();
    return calculatePerformance(energyOrCarbon, throughput);
  });

  readonly performance = computed(() => {
    const pipe = new DecimalPipe('en-GB');
    const suffix = getMeasurementAndThroughputUnits(
      this.targetComposition?.throughputUnit,
      this.sectorThroughputUnit,
      this.targetComposition.measurementType,
    );
    return `${pipe.transform(this.calculatedPerformance(), '1.0-3')} ${suffix}`;
  });

  readonly performanceSuffix = getMeasurementAndThroughputUnits(
    this.targetComposition?.throughputUnit,
    this.sectorThroughputUnit,
    this.targetComposition.measurementType,
  );

  readonly dateIsStartof2018 = computed(() => {
    return this.baselineDateValue() && this.baselineDateValue().getTime() === new Date('2018-01-01').getTime();
  });
  constructor() {
    this.form.controls.isTwelveMonths.valueChanges.pipe(takeUntilDestroyed(), distinct()).subscribe((v) => {
      if (typeof v === 'boolean') {
        this.form.get('baselineDate').reset();
        this.form.get('explanation').reset();
        this.form.get('greenfieldEvidences').reset();
        this.form.get('energy').reset();
        this.form.get('usedReportingMechanism').reset();
        this.form.get('throughput').reset();
        this.form.get('energyCarbonFactor').reset();
        this.form.updateValueAndValidity();
      }
    });
    effect(() => {
      if (this.isTwelveMonthsValue() && this.baselineDateValue() && this.dateIsStartof2018()) {
        this.form.get('explanation').reset();
      }
    });
  }
  onSubmit() {
    this.taskService
      .saveSubtask(
        this.isTargetPeriod5
          ? BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
          : BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
        BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        this.activatedRoute,
        this.form.value,
      )
      .subscribe();
  }
}
