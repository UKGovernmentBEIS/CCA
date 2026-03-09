import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';

@Component({
  selector: 'cca-underlying-agreement-regulator-led-variation-precontent',
  template: `
    @if (showNotifyAndPeerReviewButtons()) {
      <button
        netzPendingButton
        govukButton
        type="button"
        class="govuk-!-margin-right-3"
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
export class UnderlyingAgreementRegulatorLedVariationPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly sectionsCompleted = this.requestTaskStore.select(
    underlyingAgreementVariationRegulatorLedQuery.selectSectionsCompleted,
  );

  private readonly facilityItems = this.requestTaskStore.select(underlyingAgreementQuery.selectFacilityItems);

  protected readonly showNotifyAndPeerReviewButtons = computed(() => {
    if (!this.isEditable()) return false;

    const facilities = this.facilityItems();
    if (!facilities.length) return false;

    const sections = this.sectionsCompleted();
    const allowedStatuses = new Set([TaskItemStatus.UNCHANGED, TaskItemStatus.COMPLETED]);

    const allSectionsCompleted = Object.values(sections).every((status) =>
      allowedStatuses.has(status as TaskItemStatus),
    );

    const excludedIds = new Set(facilities.filter((f) => f.status === 'EXCLUDED').map((f) => f.facilityId));
    const allExcluded = facilities.every((f) => excludedIds.has(f.facilityId));

    return allSectionsCompleted && !allExcluded;
  });

  onNotifyOperatorOfDecision() {
    this.router.navigate(['underlying-agreement-variation-regulator-led', 'notify-operator'], {
      relativeTo: this.activatedRoute,
    });
  }

  onSendForPeerReview() {
    this.router.navigate(['underlying-agreement-variation-regulator-led', 'send-for-peer-review'], {
      relativeTo: this.activatedRoute,
    });
  }
}
