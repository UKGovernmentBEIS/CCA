import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, InsetTextDirective, TextareaComponent } from '@netz/govuk-components';
import {
  dontRequireOperatorAssentTypes,
  otherChangesTypes,
  OVERALL_DECISION_SUBTASK,
  requireOperatorAssentTypesWithHint,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  VARIATION_DETAILS_FORM,
  VARIATION_DETAILS_SUBTASK,
  VariationChangesTypePipe,
  VariationDetailsFormModel,
  VariationDetailsFormProvider,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

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
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<VariationDetailsFormModel>>(VARIATION_DETAILS_FORM);

  protected readonly requireOperatorAssent = requireOperatorAssentTypesWithHint;
  protected readonly dontRequireOperatorAssent = dontRequireOperatorAssentTypes;
  protected readonly otherChanges = otherChangesTypes;

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateVariationDetails(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination: resetDetermination(
        this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
      ),
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

function updateVariationDetails(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FormGroup<VariationDetailsFormModel>,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementVariationDetails = {
      ...draft.underlyingAgreementVariationDetails,
      reason: form.value.reason,
      modifications: [
        ...(draft.underlyingAgreementVariationDetails?.modifications ?? []),
        ...(form.value?.requireOperatorAssent ?? []),
        ...(form.value?.dontRequireOperatorAssent ?? []),
        ...(form.value?.otherChanges ?? []),
      ],
    };
  });
}
