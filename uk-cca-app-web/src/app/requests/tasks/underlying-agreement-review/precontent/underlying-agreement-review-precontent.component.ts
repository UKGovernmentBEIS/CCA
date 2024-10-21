import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';
import { PendingButtonDirective } from '@shared/directives';

@Component({
  selector: 'cca-underlying-agreement-review-precontent',
  template: `
    @if (isCompleted && isEditable) {
      <div class="govuk-body">
        <button ccaPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
          Notify operator of decision
        </button>
      </div>
    }
  `,
  standalone: true,
  imports: [NotificationBannerComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isCompleted = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectIsCompleted)();
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  onNotifyOperatorOfDecision() {
    this.router.navigate(['underlying-agreement-review', 'notify-operator'], { relativeTo: this.activatedRoute });
  }
}
