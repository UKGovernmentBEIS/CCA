import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import {
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionComponent,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  toDecisionNotification,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { UnderlyingAgreementReviewTaskService } from '../services/underlying-agreement-review-task.service';

@Component({
  selector: 'cca-notify-operator',
  standalone: true,
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Notify operator of decision"
      heading="Select who should receive the determination notification"
      data-testid="underlying-agreement-review-notify-operator-form"
      submitText="Confirm and complete"
    >
      <cca-notify-operator-of-decision />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WizardStepComponent,
    NotifyOperatorOfDecisionComponent,
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider],
})
export class NotifyOperatorComponent {
  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSubmit() {
    (this.taskService as UnderlyingAgreementReviewTaskService)
      .notifyOperator(toDecisionNotification(this.form.value))
      .subscribe(() => {
        this.router.navigate(['./confirmation'], { relativeTo: this.route });
      });
  }
}
