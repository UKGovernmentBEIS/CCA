import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { CancelComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { TasksService } from 'cca-api';

import { AdminTerminationCancelTaskComponent } from './admin-termination/admin-termination-cancel.component';
import { cancelTaskActionsMap } from './cancel-task-type';
import { UnderlyingAgreementCancelTaskComponent } from './underlying-agreement/underlying-agreement-application-cancel.component';
import { UnderlyingAgreementVariationCancelTaskComponent } from './underlying-agreement-variation/underlying-agreement-variation-cancel.component';

@Component({
  selector: 'cca-cancel-task',
  standalone: true,
  imports: [
    UnderlyingAgreementCancelTaskComponent,
    CancelComponent,
    AdminTerminationCancelTaskComponent,
    UnderlyingAgreementVariationCancelTaskComponent,
  ],
  templateUrl: './cancel-task.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelTaskComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly taskService = inject(TasksService);
  requestTaskType = this.store.select(requestTaskQuery.selectRequestTaskType)();

  cancel() {
    return this.taskService
      .processRequestTaskAction({
        requestTaskId: this.store.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionType: cancelTaskActionsMap[this.requestTaskType],
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      })
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
