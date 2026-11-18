import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';

import { RequestTaskActionPayload } from 'cca-api';

@Component({
  selector: 'cca-non-compliance-complete-task',
  template: `
    <div class="govuk-!-width-two-thirds">
      <netz-page-heading caption="Provide non-compliance details">Complete task</netz-page-heading>
      <p class="govuk-!-margin-bottom-9">
        By selecting 'Confirm and complete' you confirm that the task is completed. You will not be able to make any
        further changes to the task.
      </p>
      <button netzPendingButton govukButton type="button" (click)="onComplete()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceCompleteTaskComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  onComplete() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() =>
      this.router.navigate(['confirmation'], {
        relativeTo: this.activatedRoute,
        replaceUrl: true,
      }),
    );
  }
}
