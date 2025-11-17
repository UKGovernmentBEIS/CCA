import { SummaryData, SummaryFactory } from '@shared/components';
import { transformFacilityAuditReason } from '@shared/pipes';

import { AuditReasonDetails } from 'cca-api';

export function toPreAuditReviewAuditReasonSummaryData(details: AuditReasonDetails, isEditable: boolean): SummaryData {
  return new SummaryFactory()
    .addSection('', '../audit-reason')
    .addTextAreaRow('Reason for audit', details?.reasonsForAudit?.map(transformFacilityAuditReason), {
      change: isEditable,
    })
    .addTextAreaRow('Comments', details?.comment, {
      change: isEditable,
    })
    .create();
}
