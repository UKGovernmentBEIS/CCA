import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { toPreAuditReviewDeterminationSummaryData } from '../pre-audit-review-determination-summary-data';

@Component({
  selector: 'cca-pre-audit-review-determination-summary',
  template: `
    <netz-page-heading caption="Pre-audit review determination">Summary</netz-page-heading>
    <cca-summary [data]="data()" />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewDeterminationSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly auditReasonDetails = this.requestTaskStore.select(preAuditReviewQuery.selectPreAuditReviewDetails);

  protected readonly data = computed(() =>
    toPreAuditReviewDeterminationSummaryData(
      this.auditReasonDetails()?.auditDetermination,
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    ),
  );
}
