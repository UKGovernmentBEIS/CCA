import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toPreAuditReviewDeterminationSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { preAuditReviewCompletedQuery } from '../pre-audit-review-completed.selectors';

@Component({
  selector: 'cca-pre-audit-reason-completed-determination',
  template: `
    <netz-page-heading caption="Pre-audit review determination">Summary</netz-page-heading>
    <cca-summary [data]="data()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReasonCompletedDeterminationComponent {
  private readonly requestTaskActionStore = inject(RequestActionStore);

  private readonly auditReasonDetails = this.requestTaskActionStore.select(
    preAuditReviewCompletedQuery.selectPreAuditReviewDetails,
  );

  protected readonly data = computed(() =>
    toPreAuditReviewDeterminationSummaryData(this.auditReasonDetails()?.auditDetermination, false),
  );
}
