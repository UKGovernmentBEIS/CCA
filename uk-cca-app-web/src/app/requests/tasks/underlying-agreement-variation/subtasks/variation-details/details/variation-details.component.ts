import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, InsetTextDirective, TextareaComponent } from '@netz/govuk-components';
import {
  dontRequireOperatorAssentTypes,
  otherChangesTypes,
  requireOperatorAssentTypesWithHint,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  VARIATION_DETAILS_FORM,
  VARIATION_DETAILS_SUBTASK,
  VariationChangesTypePipe,
  VariationDetailsFormModel,
  VariationDetailsFormProvider,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

@Component({
  selector: 'cca-variation-details',
  templateUrl: './variation-details.component.html',
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

  protected readonly requireOperatorAssent = requireOperatorAssentTypesWithHint;
  protected readonly dontRequireOperatorAssent = dontRequireOperatorAssentTypes;
  protected readonly otherChanges = otherChangesTypes;

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateVariationDetails(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.store);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

function updateVariationDetails(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<VariationDetailsFormModel>,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementVariationDetails = {
      ...draft.underlyingAgreementVariationDetails,
      reason: form.value.reason,
      modifications: [
        ...(form.value?.requireOperatorAssent ?? []),
        ...(form.value?.dontRequireOperatorAssent ?? []),
        ...(form.value?.otherChanges ?? []),
      ],
    };
  });
}
