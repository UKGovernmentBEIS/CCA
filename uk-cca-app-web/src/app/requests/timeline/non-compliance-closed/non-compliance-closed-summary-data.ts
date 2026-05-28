import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NonComplianceClosedRequestActionPayload } from 'cca-api';

export function toNonComplianceClosedSummaryData(
  payload: NonComplianceClosedRequestActionPayload,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('')
    .addTextAreaRow('Reason for closing this task', payload.reason)
    .addFileListRow(
      'Supporting documents',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(payload.files ?? [], payload.nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
    )
    .create();
}
