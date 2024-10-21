import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';
import { TextInputComponent } from '@shared/components/text-input/text-input.component';

import { underlyingAgreementQuery } from '../../../+state';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
} from '../../../underlying-agreement.types';
import { getBaselineUnits } from '../baseline-and-targets.helper';
import { ADD_TARGETS_FORM, AddTargetsFormModel, addTargetsFormProvider } from './add-targets-form.provider';

@Component({
  selector: 'cca-add-targets',
  standalone: true,
  imports: [
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    TextInputComponent,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './add-targets.component.html',
  providers: [addTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddTargetsComponent {
  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  protected readonly isTargetPeriod5 =
    this.baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  protected readonly form = inject<AddTargetsFormModel>(ADD_TARGETS_FORM);

  protected readonly targets = toSignal(this.form.controls.target.valueChanges, {
    initialValue: this.form.value.target,
  });
  protected readonly showTargets = computed(() => typeof this.targets() === 'number');
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly baselineData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectBaselineData(this.isTargetPeriod5),
  )();

  protected readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(this.isTargetPeriod5),
  )();

  protected readonly sectorThroughputUnit = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  )()?.sectorAssociationDetails?.throughputUnit;

  readonly baselineUnitsSuffix = getBaselineUnits(
    this.targetComposition.throughputUnit,
    this.sectorThroughputUnit,
    this.targetComposition.measurementType,
    this.targetComposition.agreementCompositionType,
  );

  onSubmit() {
    this.taskService
      .saveSubtask(
        this.isTargetPeriod5
          ? BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
          : BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
        BaseLineAndTargetsStep.ADD_TARGETS,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
