import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationQuery } from '../+state/admin-termination.selectors';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../admin-termination.types';

@Component({
  selector: 'cca-admin-termination-precontent',
  template: `
    @if (isRegulatoryReasonSelected) {
      <govuk-notification-banner heading="Important">
        <h3>Admin termination details updated</h3>

        <p id="notification-content">
          Once you notify the operator, you must allow at least 28 days for the operator to appeal the decision to
          terminating their agreement. After this time window you may start the final decision for the termination
          workflow.
        </p>
      </govuk-notification-banner>
    }

    @if (isReasonForAdminTerminationCompleted && isEditable()) {
      <button
        class="govuk-!-margin-right-3"
        netzPendingButton
        govukButton
        type="button"
        (click)="onSendForPeerReview()"
      >
        Send for peer review
      </button>

      <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
        Notify operator of decision
      </button>
    }
  `,
  standalone: true,
  imports: [NotificationBannerComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly adminTerminationReasonDetails = this.requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationReasonDetails,
  )();

  private readonly adminTerminationSectionsCompleted = this.requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationSectionsCompleted,
  )();

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly isRegulatoryReasonSelected =
    !!this.adminTerminationReasonDetails &&
    (this.adminTerminationReasonDetails.reason === 'FAILURE_TO_COMPLY' ||
      this.adminTerminationReasonDetails.reason === 'FAILURE_TO_AGREE' ||
      this.adminTerminationReasonDetails.reason === 'FAILURE_TO_PAY');

  protected readonly isReasonForAdminTerminationCompleted =
    this.adminTerminationSectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.COMPLETED;

  onNotifyOperatorOfDecision() {
    this.router.navigate(['admin-termination', 'notify-operator'], { relativeTo: this.activatedRoute });
  }

  onSendForPeerReview() {
    this.router.navigate(['admin-termination', 'send-for-peer-review'], { relativeTo: this.activatedRoute });
  }
}
