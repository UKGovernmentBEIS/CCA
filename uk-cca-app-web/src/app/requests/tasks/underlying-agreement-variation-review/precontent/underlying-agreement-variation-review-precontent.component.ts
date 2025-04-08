import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';
import { ConfigService } from '@shared/config';

@Component({
  selector: 'cca-underlying-variation-agreement-review-precontent',
  template: `
    @if (determinationSubmitted && isEditable && notifyEnabled) {
      <div class="govuk-body">
        <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
          Notify operator of decision
        </button>
      </div>
    }
  `,
  standalone: true,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly config = inject(ConfigService);

  readonly notifyEnabled = !this.config.isFeatureEnabled('unaHideNotifyOperator');

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  readonly determinationSubmitted = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectDeterminationSubmitted,
  )();

  onNotifyOperatorOfDecision() {
    this.router.navigate(['underlying-agreement-variation-review', 'notify-operator'], {
      relativeTo: this.activatedRoute,
    });
  }
}
