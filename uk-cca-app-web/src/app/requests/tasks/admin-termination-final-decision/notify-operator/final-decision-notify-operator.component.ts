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
  toCcaDecisionNotification,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { adminTerminationFinalDecisionQuery } from '../admin-termination-final-decision.selectors';
import { createNotifyOperatorActionDTO } from '../transform';

@Component({
  selector: 'cca-final-decision-notify-operator',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Notify operator of decision"
      [heading]="heading"
      data-testid="admin-termination-final-decision-notify-operator-form"
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
export default class FinalDecisionNotifyOperatorComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);

  private readonly finalDecisionType = this.requestTaskStore.select(
    adminTerminationFinalDecisionQuery.selectReasonDetails,
  )()?.finalDecisionType;

  protected readonly heading =
    this.finalDecisionType === 'TERMINATE_AGREEMENT'
      ? 'Select who should receive the termination notice'
      : 'Select who should receive the admin termination withdrawal notice';

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toCcaDecisionNotification(this.form.value);
    const dto = createNotifyOperatorActionDTO(requestTaskId, notification);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
