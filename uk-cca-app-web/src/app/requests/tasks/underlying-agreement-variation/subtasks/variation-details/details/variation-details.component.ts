import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, InsetTextDirective, TextareaComponent } from '@netz/govuk-components';
import {
  baselineChangesTypesOption,
  facilityChangesTypes,
  otherChangesTypes,
  targetCurrencyChangesTypes,
  TasksApiService,
  underlyingAgreementQuery,
  VARIATION_DETAILS_SUBTASK,
  VariationChangesTypePipe,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';
import {
  VARIATION_DETAILS_FORM,
  VariationDetailsFormModel,
  VariationDetailsFormProvider,
} from './variation-details-form.provider';

@Component({
  selector: 'cca-variation-details',
  templateUrl: './variation-details.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextareaComponent,
    CheckboxComponent,
    CheckboxesComponent,
    VariationChangesTypePipe,
    InsetTextDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [VariationDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  protected readonly store = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);
  protected readonly form = inject<FormGroup<VariationDetailsFormModel>>(VARIATION_DETAILS_FORM);

  protected readonly facilityChanges = facilityChangesTypes;
  protected readonly baselineChanges = baselineChangesTypesOption;
  protected readonly targetCurrencyChanges = targetCurrencyChangesTypes;
  protected readonly otherChanges = otherChangesTypes;

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.underlyingAgreementVariationDetails = {
        reason: this.form.value.reason,
        modifications: [
          ...(this.form.value?.facilityChanges ?? []),
          ...(this.form.value?.baselineChanges ?? []),
          ...(this.form.value?.targetCurrencyChanges ?? []),
          ...(this.form.value?.otherChanges ?? []),
        ],
      };
    });

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[VARIATION_DETAILS_SUBTASK] = 'IN_PROGRESS';
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.store);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}
