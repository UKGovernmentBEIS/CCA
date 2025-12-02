import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toPreAuditReviewAuditReasonSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';

@Component({
  selector: 'cca-pre-audit-review-audit-review-summary',
  template: `
    <netz-page-heading caption="Audit reason">Summary</netz-page-heading>
    <cca-summary [data]="data()" />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewAuditReasonSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly auditReasonDetails = this.requestTaskStore.select(preAuditReviewQuery.selectPreAuditReviewDetails);

  protected readonly data = computed(() =>
    toPreAuditReviewAuditReasonSummaryData(
      this.auditReasonDetails()?.auditReasonDetails,
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    ),
  );
}
