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

import { UnderlyingAgreementVariationActivationTaskService } from '../services/underlying-agreement-variation-activation-task.service';

@Component({
  selector: 'cca-underlying-agreement-variation-activation-notify-operator',
  templateUrl: './underlying-agreement-variation-activation-notify-operator.component.html',
  standalone: true,
  imports: [
    NotifyOperatorOfDecisionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementVariationActivationNotifyOperatorComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);

  onSubmit() {
    (this.taskService as UnderlyingAgreementVariationActivationTaskService)
      .notifyOperator(toDecisionNotification(this.form.value))
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
