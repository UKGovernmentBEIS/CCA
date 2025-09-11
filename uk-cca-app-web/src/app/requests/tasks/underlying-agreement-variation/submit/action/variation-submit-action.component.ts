import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';

import { createSubmitActionDTO } from '../../transform';

@Component({
  selector: 'cca-una-variation-submit-action',
  template: `
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
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    WarningTextComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationSubmitActionComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSubmitActionDTO(requestTaskId);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
