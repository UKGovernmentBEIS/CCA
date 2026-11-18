import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators, TextareaComponent } from '@netz/govuk-components';
import { TasksApiService, underlyingAgreementReviewQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { createSaveDeterminationActionDTO } from '../../../transform';
import { resetDeterminationStatus } from '../../../utils';

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
  private readonly fb = inject(FormBuilder);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  protected readonly form = this.fb.group({
    reason: this.fb.control(this.determination.reason, [
      GovukValidators.required('Enter a reason for rejecting.'),
      GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
    ]),
  });

  submit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const currReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = resetDeterminationStatus(currReviewSectionsCompleted);

    const payload = createSaveDeterminationActionDTO(
      requestTaskId,
      { ...this.determination, reason: this.form.value.reason },
      reviewSectionsCompleted,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      });
    });
  }
}
