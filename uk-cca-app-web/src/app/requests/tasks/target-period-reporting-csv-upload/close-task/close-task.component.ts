import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

@Component({
  selector: 'cca-close-task',
  template: `
    <div class="govuk-grid-row">
      <netz-page-heading size="xl">Are you sure you want to close this task?</netz-page-heading>
      <div class="govuk-button-group">
        <button type="button" netzPendingButton (click)="onClose()" govukWarnButton>Yes, close this task</button>
      </div>
    </div>
  `,
  imports: [PendingButtonDirective, PageHeadingComponent, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CloseTaskComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  onClose() {
    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload,
      })
      .subscribe(() =>
        this.router.navigate(['..', 'close-confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }),
      );
  }
}
