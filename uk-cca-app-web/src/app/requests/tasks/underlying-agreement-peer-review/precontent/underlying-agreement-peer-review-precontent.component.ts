import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-peer-review-precontent',
  template: `
    @if (isUserAssignee()) {
      <button
        netzPendingButton
        govukButton
        type="button"
        routerLink="underlying-agreement-peer-review/peer-review-decision"
        [relativeTo]="activatedRoute"
        [replaceUrl]="true"
      >
        Peer review decision
      </button>
    }
  `,
  standalone: true,
  imports: [ButtonDirective, PendingButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementPeerReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly authStore = inject(AuthStore);
  protected readonly activatedRoute = inject(ActivatedRoute);

  private readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();
  protected readonly isUserAssignee = computed(() => this.authStore.select(selectUserId)() === this.assigneeUserId);
}
