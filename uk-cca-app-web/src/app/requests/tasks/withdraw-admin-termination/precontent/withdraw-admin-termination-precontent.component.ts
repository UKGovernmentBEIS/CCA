import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationWithdrawQuery } from '../+state/withdraw-admin-termination.selectors';
import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../withdraw-admin-termination.types';

@Component({
  selector: 'cca-withdraw-admin-termination-precontent',
  template: `
    @if (isReasonForWithdrawAdminTerminationCompleted && isEditable) {
      <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
        Notify operator of decision
      </button>
    }
  `,
  standalone: true,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawAdminTerminationPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly withdrawAdminTerminationSectionsCompleted = this.requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationSectionsCompleted,
  )();

  protected readonly isReasonForWithdrawAdminTerminationCompleted =
    this.withdrawAdminTerminationSectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ===
    TaskItemStatus.COMPLETED;

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  onNotifyOperatorOfDecision() {
    this.router.navigate(['withdraw-admin-termination', 'withdraw-notify-operator'], {
      relativeTo: this.activatedRoute,
    });
  }
}
