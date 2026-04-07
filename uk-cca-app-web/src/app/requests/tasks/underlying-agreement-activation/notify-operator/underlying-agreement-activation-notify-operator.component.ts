import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ErrorSummaryComponent } from '@netz/govuk-components';
import {
  API_ERROR_FORM,
  ApiErrorFormModel,
  ApiErrorFormProvider,
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionComponent,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  setApiErrors,
  TasksApiService,
  toCcaDecisionNotification,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { createNotifyOperatorActionDTO } from '../transform';

@Component({
  selector: 'cca-underlying-agreement-activation-notify-operator',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="errorForm" />
    }

    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Notify operator"
      heading="Select who should receive the active Underlying Agreement notice"
      data-testid="underlying-agreement-activation-notify-operator-form"
      submitText="Confirm and complete"
    >
      <cca-notify-operator-of-decision />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ErrorSummaryComponent,
    NotifyOperatorOfDecisionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider, ApiErrorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementActivationNotifyOperatorComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);
  protected readonly errorForm = inject<ApiErrorFormModel>(API_ERROR_FORM);
  protected readonly isErrorSummaryDisplayed = signal(false);

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toCcaDecisionNotification(this.form.value);
    const dto = createNotifyOperatorActionDTO(requestTaskId, notification);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchBadRequest(ErrorCodes.UNA1001, (res) => {
          setApiErrors(this.errorForm, res);
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
