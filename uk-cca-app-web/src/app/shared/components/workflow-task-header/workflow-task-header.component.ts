import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

import { of, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { FacilityHeaderInfoDTO, TargetUnitAccountHeaderInfoDTO, TasksService } from 'cca-api';

@Component({
  selector: 'cca-workflow-task-header',
  templateUrl: './workflow-task-header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowTaskHeaderComponent {
  private readonly tasksService = inject(TasksService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly headerInfo: Signal<TargetUnitAccountHeaderInfoDTO | FacilityHeaderInfoDTO> = toSignal(
    toObservable(this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)).pipe(
      switchMap((requestInfo) =>
        requestInfo
          ? this.tasksService.getRequestTaskHeaderInfo(
              requestInfo?.resourceType,
              requestInfo?.resources?.[requestInfo?.resourceType],
            )
          : of(null),
      ),
    ),
  );
}
