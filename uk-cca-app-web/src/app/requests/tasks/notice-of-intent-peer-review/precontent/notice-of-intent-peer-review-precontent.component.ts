import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';

@Component({
  selector: 'cca-notice-of-intent-peer-review-precontent',
  template: `
    @if (isUserAssignee()) {
      <button
        netzPendingButton
        govukButton
        type="button"
        routerLink="notice-of-intent-peer-review/peer-review-decision"
        [relativeTo]="activatedRoute"
        [replaceUrl]="true"
      >
        Peer review decision
      </button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoticeOfIntentPeerReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly authStore = inject(AuthStore);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId);
  private readonly userId = this.authStore.select(selectUserId);
  protected readonly isUserAssignee = computed(() => this.userId() === this.assigneeUserId());
}
