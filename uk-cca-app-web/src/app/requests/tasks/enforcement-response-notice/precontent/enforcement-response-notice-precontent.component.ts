import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { enforcementResponseNoticeQuery } from '../enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from '../enforcement-response-notice.types';

@Component({
  selector: 'cca-enforcement-response-notice-precontent',
  template: `
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
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnforcementResponseNoticePrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly sectionsCompleted = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectSectionsCompleted,
  );
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly isCompleted = computed(
    () => this.sectionsCompleted()?.[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onNotifyOperatorOfDecision() {
    this.router.navigate(['enforcement-response-notice', 'notify-operator'], { relativeTo: this.activatedRoute });
  }

  onSendForPeerReview() {
    this.router.navigate(['enforcement-response-notice', 'send-for-peer-review'], { relativeTo: this.activatedRoute });
  }
}
