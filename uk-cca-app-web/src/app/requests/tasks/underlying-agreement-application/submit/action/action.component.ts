import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';

import { RequestTaskActionProcessDTO } from 'cca-api';

@Component({
  selector: 'cca-una-submit-action',
  template: `
    <netz-page-heading size="xl"> Submit to regulator </netz-page-heading>

    <p>Your application will be sent directly to your Regulator (Environment Agency).</p>
    <p>
      By selecting 'Confirm and send' you confirm that the information in your application is correct to the best of
      your knowledge.
    </p>

    <div class="govuk-button-group">
      <button type="button" netzPendingButton (click)="submit()" govukButton>Confirm and send</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [ButtonDirective, PageHeadingComponent, PendingButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementSubmitActionComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto: RequestTaskActionProcessDTO = {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['confirmation'], { relativeTo: this.route });
    });
  }
}
