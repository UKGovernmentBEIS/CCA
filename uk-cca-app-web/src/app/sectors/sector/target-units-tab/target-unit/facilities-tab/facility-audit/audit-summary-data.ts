import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformFacilityAuditReason } from '@shared/pipes';

import { FacilityAuditViewDTO } from 'cca-api';

export function toFacilityAuditSummaryData(audit: FacilityAuditViewDTO): SummaryData {
  const factory = new SummaryFactory()
    .addSection('Facility audit', null, { testid: 'facility-audit' })
    .addRow('Should this facility be considered for audit?', boolToString(audit?.auditRequired), {
      change: audit?.editable,
      changeLink: './audit',
    });

  if (audit?.auditRequired) {
    factory
      .addTextAreaRow('Reason for audit', audit.reasons.map(transformFacilityAuditReason), {
        change: audit?.editable,
        changeLink: './audit/reasons',
      })
      .addTextAreaRow('Comments', audit.comments, {
        change: audit?.editable,
        changeLink: './audit/reasons',
      });
  }

  return factory.create();
}
