import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toPreAuditReviewAuditReasonSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { preAuditReviewCompletedQuery } from '../pre-audit-review-completed.selectors';

@Component({
  selector: 'cca-pre-audit-review-completed-reason',
  template: `
    <netz-page-heading caption="Pre-audit review reason">Summary</netz-page-heading>
    <cca-summary [data]="data()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewCompletedReasonComponent {
  private readonly requestTaskActionStore = inject(RequestActionStore);

  private readonly auditReasonDetails = this.requestTaskActionStore.select(
    preAuditReviewCompletedQuery.selectPreAuditReviewDetails,
  );

  protected readonly data = computed(() =>
    toPreAuditReviewAuditReasonSummaryData(this.auditReasonDetails()?.auditReasonDetails, false),
  );
}
