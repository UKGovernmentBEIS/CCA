import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';

@Component({
  selector: 'cca-underlying-variation-agreement-review-precontent',
  template: `
    @if (determinationSubmitted() && isEditable()) {
      <button
        netzPendingButton
        govukButton
        type="button"
        class="govuk-!-margin-right-3"
        (click)="onSendForPeerReview()"
      >
        Send for peer review
      </button>
    }

    @if (determinationSubmitted() && isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
        Notify operator of decision
      </button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly determinationSubmitted = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectDeterminationSubmitted,
  );

  onNotifyOperatorOfDecision() {
    this.router.navigate(['underlying-agreement-variation-review', 'notify-operator'], {
      relativeTo: this.activatedRoute,
    });
  }

  onSendForPeerReview() {
    this.router.navigate(['underlying-agreement-variation-review', 'send-for-peer-review'], {
      relativeTo: this.activatedRoute,
    });
  }
}
