import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { adminTerminationQuery } from '../admin-termination.selectors';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../types';

@Component({
  selector: 'cca-admin-termination-precontent',
  template: `
    @if (isRegulatoryReasonSelected()) {
      <govuk-notification-banner heading="Important">
        <h3>Admin termination details updated</h3>

        <p id="notification-content">
          Once you notify the operator, you must allow at least 28 days for the operator to appeal the decision to
          terminating their agreement. After this time window you may start the final decision for the termination
          workflow.
        </p>
      </govuk-notification-banner>
    }

    @if (isCompleted() && isEditable()) {
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
  imports: [NotificationBannerComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly reasonDetails = this.requestTaskStore.select(adminTerminationQuery.selectReasonDetails);
  private readonly sectionsCompleted = this.requestTaskStore.select(adminTerminationQuery.selectSectionsCompleted);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly isRegulatoryReasonSelected = computed(
    () =>
      !!this.reasonDetails() &&
      ['FAILURE_TO_COMPLY', 'FAILURE_TO_AGREE', 'FAILURE_TO_PAY'].includes(this.reasonDetails().reason),
  );

  protected readonly isCompleted = computed(
    () => this.sectionsCompleted()[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onNotifyOperatorOfDecision() {
    this.router.navigate(['admin-termination', 'notify-operator'], { relativeTo: this.activatedRoute });
  }

  onSendForPeerReview() {
    this.router.navigate(['admin-termination', 'send-for-peer-review'], { relativeTo: this.activatedRoute });
  }
}
