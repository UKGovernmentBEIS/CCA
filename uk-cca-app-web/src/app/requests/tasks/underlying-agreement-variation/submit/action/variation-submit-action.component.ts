import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, ErrorSummaryComponent, WarningTextComponent } from '@netz/govuk-components';
import {
  API_ERROR_FORM,
  ApiErrorFormModel,
  ApiErrorFormProvider,
  setApiErrors,
  TasksApiService,
} from '@requests/common';

import { createSubmitActionDTO } from '../../transform';

@Component({
  selector: 'cca-una-variation-submit-action',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="errorForm" />
    }

    <netz-page-heading size="xl">Send variation application to regulator</netz-page-heading>

    <govuk-warning-text assistiveText="">
      You will not be able to make any changes until the regulator has completed the review.
    </govuk-warning-text>

    <p>
      By selecting 'Confirm and send' you confirm that the information in your variation application is correct to the
      best of your knowledge.
    </p>

    <div class="govuk-button-group">
      <button type="button" netzPendingButton (click)="submit()" govukButton>Confirm and send</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ButtonDirective,
    ErrorSummaryComponent,
    PageHeadingComponent,
    PendingButtonDirective,
    ReactiveFormsModule,
    WarningTextComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ApiErrorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationSubmitActionComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly errorForm = inject<ApiErrorFormModel>(API_ERROR_FORM);
  protected readonly isErrorSummaryDisplayed = signal(false);

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSubmitActionDTO(requestTaskId);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchBadRequest(ErrorCodes.UNAV1001, (res) => {
          setApiErrors(this.errorForm, res);
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
