import { Component, inject, signal } from '@angular/core';
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
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { AnalyticsService } from '@shared/services';

import { createSubmitActionDTO } from '../../transform';

@Component({
  selector: 'cca-una-variation-submit-action',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="errorForm" />
    }

    <netz-page-heading size="xl">Send variation application to regulator</netz-page-heading>

    <govuk-warning-text>
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
  providers: [ApiErrorFormProvider],
  imports: [
    ButtonDirective,
    ErrorSummaryComponent,
    PageHeadingComponent,
    PendingButtonDirective,
    ReactiveFormsModule,
    WarningTextComponent,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class VariationSubmitActionComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly errorForm = inject<ApiErrorFormModel>(API_ERROR_FORM);
  protected readonly isErrorSummaryDisplayed = signal(false);

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSubmitActionDTO(requestTaskId);
    const modifications =
      this.store.select(underlyingAgreementVariationQuery.selectVariationDetails)()?.modifications ?? [];

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchBadRequest(ErrorCodes.UNAV1001, (res) => {
          setApiErrors(this.errorForm, res);
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.analyticsService.push({
          event: 'journey_outcome',
          journey_id: 'J_016',
          task_name: 'cca_variation',
          task_outcome: 'submitted',
          variation_modifications: modifications.map((modification) => modification.toLowerCase()).join('|'),
          variation_modification_count: modifications.length,
        });

        this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
