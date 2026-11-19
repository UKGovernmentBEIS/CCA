import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils, formatCalendarDate } from '@shared/utils';

import { AuditDetails } from 'cca-api';

export function toAuditDetailsSummaryData(
  auditDetails: AuditDetails,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../details')
    .addRow(
      'Audit technique',
      auditDetails?.auditTechnique === 'DESK_BASED_INTERVIEW' ? 'Desk-based audit' : 'On-site visit',
      {
        change: isEditable,
      },
    )
    .addRow('Audit date', formatCalendarDate(auditDetails?.auditDate, 'dd MMM yyyy'), {
      change: isEditable,
    })
    .addTextAreaRow('Comments', auditDetails?.comments, {
      change: isEditable,
    })
    .addRow('Date final audit report sent', formatCalendarDate(auditDetails?.finalAuditReportDate, 'dd MMM yyyy'), {
      change: isEditable,
    })
    .addFileListRow(
      'Audit documents',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(auditDetails?.auditDocuments, attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
