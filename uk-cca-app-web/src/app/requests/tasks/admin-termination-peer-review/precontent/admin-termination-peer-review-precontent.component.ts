import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';

@Component({
  selector: 'cca-admin-termination-peer-review-precontent',
  template: `
    @if (isUserAssignee()) {
      <a
        govukButton
        routerLink="admin-termination-peer-review/peer-review-decision"
        [relativeTo]="activatedRoute"
        [replaceUrl]="true"
      >
        Peer review decision
      </a>
    }
  `,
  imports: [ButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationPeerReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly authStore = inject(AuthStore);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId);
  protected readonly isUserAssignee = computed(() => this.authStore.select(selectUserId)() === this.assigneeUserId());
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
}
