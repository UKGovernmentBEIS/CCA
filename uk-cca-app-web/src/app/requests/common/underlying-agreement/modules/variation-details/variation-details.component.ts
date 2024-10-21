import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import {
  CheckboxComponent,
  CheckboxesComponent,
  InsetTextDirective,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { VariationChangesTypePipe } from '../../pipes/variation-changes-type.pipe';
import { VARIATION_DETAILS_SUBTASK, VariationDetailsWizardStep } from '../../underlying-agreement.types';
import {
  baselineChangesTypesOption,
  facilityChangesTypes,
  otherChangesTypes,
  targetCurrencyChangesTypes,
} from './variation-details.helper';
import {
  VARIATION_DETAILS_FORM,
  VariationDetailsFormModel,
  VariationDetailsFormProvider,
} from './variation-details-form.provider';

@Component({
  selector: 'cca-variation-details',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    TextareaComponent,
    CheckboxComponent,
    CheckboxesComponent,
    VariationChangesTypePipe,
    InsetTextDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './variation-details.component.html',
  providers: [VariationDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<VariationDetailsFormModel>>(VARIATION_DETAILS_FORM);

  protected readonly facilityChanges = facilityChangesTypes;
  protected readonly baselineChanges = baselineChangesTypesOption;
  protected readonly targetCurrencyChanges = targetCurrencyChangesTypes;
  protected readonly otherChanges = otherChangesTypes;

  onSubmit() {
    this.taskService
      .saveSubtask(VARIATION_DETAILS_SUBTASK, VariationDetailsWizardStep.DETAILS, this.activatedRoute, {
        reason: this.form.value.reason,
        modifications: [
          ...(this.form.value?.facilityChanges ?? []),
          ...(this.form.value?.baselineChanges ?? []),
          ...(this.form.value?.targetCurrencyChanges ?? []),
          ...(this.form.value?.otherChanges ?? []),
        ],
      })
      .subscribe();
  }
}
