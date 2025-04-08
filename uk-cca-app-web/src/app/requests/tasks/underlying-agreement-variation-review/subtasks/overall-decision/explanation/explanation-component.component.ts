import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators, TextareaComponent } from '@netz/govuk-components';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-explanation-component',
  standalone: true,
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
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [WizardStepComponent, TextareaComponent, ReactiveFormsModule, ReturnToTaskOrActionPageComponent],
})
export class ExplanationComponentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  protected readonly form = this.fb.group({
    reason: this.fb.control(this.determination.reason, GovukValidators.required('Enter a reason for rejecting.')),
  });

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveReviewDetermination({ reason: this.form.value.reason })
      .subscribe(() =>
        this.router.navigate(['../', OverallDecisionWizardStep.ADDITIONAL_INFO], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'preserve',
        }),
      );
  }
}
