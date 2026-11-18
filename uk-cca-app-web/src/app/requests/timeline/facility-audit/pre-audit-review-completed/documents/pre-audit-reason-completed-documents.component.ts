import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toPreAuditReviewRequestedDocumentsSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { preAuditReviewCompletedQuery } from '../pre-audit-review-completed.selectors';

@Component({
  selector: 'cca-pre-audit-reason-completed-documents',
  template: `
    <netz-page-heading caption="Requested documents">Summary</netz-page-heading>
    <cca-summary [data]="data()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReasonCompletedDocumentsComponent {
  private readonly requestTaskActionStore = inject(RequestActionStore);

  private readonly auditReasonDetails = this.requestTaskActionStore.select(
    preAuditReviewCompletedQuery.selectPreAuditReviewDetails,
  );

  private readonly attachments = this.requestTaskActionStore.select(
    preAuditReviewCompletedQuery.selectFacilityAuditAttachments,
  );

  protected readonly data = computed(() =>
    toPreAuditReviewRequestedDocumentsSummaryData(
      this.auditReasonDetails()?.requestedDocuments,
      this.attachments(),
      false,
      '../../file-download',
    ),
  );
}
