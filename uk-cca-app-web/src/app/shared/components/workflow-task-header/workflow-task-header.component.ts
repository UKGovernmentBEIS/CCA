import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

import { map, of, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { TargetUnitAccountInfoViewService } from 'cca-api';

@Component({
  selector: 'cca-workflow-task-header',
  templateUrl: './workflow-task-header.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowTaskHeaderComponent {
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly accountHeaderInfo = toSignal(
    toObservable(this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)).pipe(
      map((requestInfo) => requestInfo?.accountId),
      switchMap((accountId) =>
        accountId ? this.targetUnitAccountInfoViewService.getAccountHeaderInfoById(accountId) : of(null),
      ),
    ),
  );
}
