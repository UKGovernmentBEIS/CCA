import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators, TextareaComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  OverallDecisionWizardStep,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveDeterminationActionDTO } from '../../../transform';

@Component({
  selector: 'cca-explanation-component',
  template: `
    <cca-wizard-step
      [formGroup]="form"
      caption="Reject"
      heading="Explain why you are rejecting the application"
      (formSubmit)="submit()"
    >
      <div govuk-textarea formControlName="reason" hint="This will be included in the official notice."></div>
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [WizardStepComponent, TextareaComponent, ReactiveFormsModule, ReturnToTaskOrActionPageComponent],
})
export class ExplanationComponentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  protected readonly form = this.fb.group({
    reason: this.fb.control(this.determination.reason, [
      GovukValidators.required('Enter a reason for rejecting.'),
      GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
    ]),
  });

  submit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const updatedDetermination = produce(this.determination, (draft) => {
      draft.reason = this.form.value.reason;
    });

    const dto = createSaveDeterminationActionDTO(requestTaskId, updatedDetermination, reviewSectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() =>
      this.router.navigate(['../', OverallDecisionWizardStep.ADDITIONAL_INFO], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      }),
    );
  }
}
