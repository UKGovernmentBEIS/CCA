import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationFinalDecisionQuery } from '../+state/admin-termination-final-decision.selectors';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../admin-termination-final-decision.helper';

@Component({
  selector: 'cca-admin-termination-final-decision-precontent',
  template: `
    @if (isFinalDecisionReasonCompleted && isEditable) {
      <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
        Notify operator of decision
      </button>
    }
  `,
  standalone: true,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationFinalDecisionPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly adminTerminationFinalDecisionSectionsCompleted = this.requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionSectionsCompleted,
  )();

  protected readonly isFinalDecisionReasonCompleted =
    this.adminTerminationFinalDecisionSectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] ===
    TaskItemStatus.COMPLETED;

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  onNotifyOperatorOfDecision() {
    this.router.navigate(['admin-termination-final-decision', 'final-decision-notify-operator'], {
      relativeTo: this.activatedRoute,
      replaceUrl: true,
    });
  }
}
