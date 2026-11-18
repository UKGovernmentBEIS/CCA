import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { InsetTextDirective } from '@netz/govuk-components';
import {
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionComponent,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  TasksApiService,
  toNonComplianceDecisionNotification,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { createNotifyOperatorActionDTO } from '../transform';

@Component({
  selector: 'cca-notice-of-intent-notify-operator',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Notify operator of decision"
      heading="Select who should receive the notice of intent notification"
      data-testid="notice-of-intent-notify-operator-form"
      submitText="Confirm and complete"
    >
      <cca-notify-operator-of-decision />

      <div govukInsetText>
        <p>Make sure to review your uploaded notice before proceeding.</p>
        <ul class="govuk-list govuk-list--bullet govuk-!-margin-bottom-0">
          <li>Referenced dates may be outdated</li>
          <li>Ensure all details are current</li>
        </ul>
      </div>
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    NotifyOperatorOfDecisionComponent,
    ReactiveFormsModule,
    InsetTextDirective,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class NoticeOfIntentNotifyOperatorComponent {
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toNonComplianceDecisionNotification(this.form.value);
    const dto = createNotifyOperatorActionDTO(requestTaskId, notification);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
