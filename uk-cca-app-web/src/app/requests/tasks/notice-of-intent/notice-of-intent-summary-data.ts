import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NoticeOfIntent } from 'cca-api';

export function toNoticeOfIntentSummaryData(
  noticeOfIntent: NoticeOfIntent | undefined,
  nonComplianceAttachments: Record<string, string> | undefined,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../upload-notice')
    .addFileListRow(
      'Upload file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([noticeOfIntent?.noticeOfIntentFile], nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addTextAreaRow('Your comments', noticeOfIntent?.comments ?? '', { change: isEditable })
    .create();
}
