import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NonComplianceNoticeOfIntent } from 'cca-api';

export function toNoticeOfIntentSummaryData(
  noticeOfIntent: NonComplianceNoticeOfIntent | undefined,
  nonComplianceAttachments: Record<string, string> | undefined,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../upload-notice')
    .addFileListRow(
      'Upload file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([noticeOfIntent?.file], nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addTextAreaRow('Your comments', noticeOfIntent?.comments ?? '', { change: isEditable })
    .create();
}
