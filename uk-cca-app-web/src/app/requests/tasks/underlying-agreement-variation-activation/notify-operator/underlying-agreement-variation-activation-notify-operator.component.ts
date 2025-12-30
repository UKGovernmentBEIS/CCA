import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionComponent,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  TasksApiService,
  toDecisionNotification,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { createNotifyOperatorActionDTO } from '../transform';

@Component({
  selector: 'cca-underlying-agreement-variation-activation-notify-operator',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Notify operator"
      heading="Select who should be notified of the decision"
      submitText="Confirm and complete"
    >
      <cca-notify-operator-of-decision />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
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
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toDecisionNotification(this.form.value);
    const dto = createNotifyOperatorActionDTO(requestTaskId, notification);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
