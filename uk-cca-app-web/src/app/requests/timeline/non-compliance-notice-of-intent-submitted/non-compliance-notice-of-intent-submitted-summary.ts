import { extractOperatorUsersFromUsersInfo, transformUserContacts } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  DecisionNotification,
  DefaultNoticeRecipient,
  NonComplianceNoticeOfIntent,
  RequestActionUserInfo,
} from 'cca-api';

export function toNonComplianceNoticeOfIntentSubmittedSummaryData(
  noticeOfIntent: NonComplianceNoticeOfIntent | undefined,
  nonComplianceAttachments: Record<string, string> | undefined,
  defaultContacts: DefaultNoticeRecipient[],
  downloadUrl: string,
  decisionNotification?: DecisionNotification,
  usersInfo?: Record<string, RequestActionUserInfo>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addFileListRow(
      'Upload file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([noticeOfIntent?.file], nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
    )
    .addRow('Comments', noticeOfIntent?.comments)
    .addSection('Official notice recipients')
    .addTextAreaRow('Users notified', [
      ...transformUserContacts(defaultContacts),
      ...extractOperatorUsersFromUsersInfo(usersInfo ?? {}, decisionNotification?.operators),
    ])
    .create();
}
