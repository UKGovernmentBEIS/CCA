import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, of, switchMap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { createSubmitActionDTO } from '../transform';

@Component({
  selector: 'cca-complete-task',
  template: `
    <div class="govuk-!-width-two-thirds">
      <netz-page-heading caption="Track corrective actions">Complete task</netz-page-heading>
      <p class="govuk-!-margin-bottom-9">
        By selecting 'Confirm and complete' you confirm that the task is completed. You will not be able to make any
        further changes to the task.
      </p>
      <button netzPendingButton govukButton type="button" (click)="onComplete()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../..">
      Return to: Track corrective actions {{ targetUnitAccountDetails()?.targetUnitAccountDetails?.businessId }}
    </a>
  `,
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteTaskComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);

  protected readonly targetUnitAccountDetails = toSignal(
    toObservable(this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)).pipe(
      map((requestInfo) => requestInfo?.accountId),
      switchMap((accountId) =>
        accountId ? this.targetUnitAccountInfoViewService.getTargetUnitAccountDetailsById(accountId) : of(null),
      ),
    ),
  );

  onComplete() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSubmitActionDTO(requestTaskId);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() =>
      this.router.navigate(['confirmation'], {
        relativeTo: this.activatedRoute,
        replaceUrl: true,
      }),
    );
  }
}
