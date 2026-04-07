import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { noticeOfIntentQuery } from '../notice-of-intent.selectors';
import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from '../notice-of-intent.types';

@Component({
  selector: 'cca-notice-of-intent-precontent',
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
export class NoticeOfIntentPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly sectionsCompleted = this.requestTaskStore.select(noticeOfIntentQuery.selectSectionsCompleted);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly isCompleted = computed(
    () => this.sectionsCompleted()?.[UPLOAD_NOTICE_OF_INTENT_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onNotifyOperatorOfDecision() {
    this.router.navigate(['notice-of-intent', 'notify-operator'], { relativeTo: this.activatedRoute });
  }

  onSendForPeerReview() {
    this.router.navigate(['notice-of-intent', 'send-for-peer-review'], { relativeTo: this.activatedRoute });
  }
}
