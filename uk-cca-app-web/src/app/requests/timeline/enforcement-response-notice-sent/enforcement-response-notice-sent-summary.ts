import {
  ENFORCEMENT_RESPONSE_NOTICE_TYPE_LABELS,
  extractOperatorUsersFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  DecisionNotification,
  DefaultNoticeRecipient,
  NonComplianceEnforcementResponseNotice,
  RequestActionUserInfo,
} from 'cca-api';

export function toEnforcementResponseNoticeSentSummaryData(
  enforcementResponseNotice: NonComplianceEnforcementResponseNotice | undefined,
  nonComplianceAttachments: Record<string, string> | undefined,
  defaultContacts: DefaultNoticeRecipient[],
  downloadUrl: string,
  decisionNotification?: DecisionNotification,
  usersInfo?: Record<string, RequestActionUserInfo>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addRow(
      'Type of enforcement response notice',
      enforcementResponseNotice?.type ? ENFORCEMENT_RESPONSE_NOTICE_TYPE_LABELS[enforcementResponseNotice.type] : '',
    )
    .addFileListRow(
      'Upload file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([enforcementResponseNotice?.file], nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
    )
    .addRow('Your comments', enforcementResponseNotice?.comments)
    .addSection('Official notice recipients')
    .addTextAreaRow('Users notified', [
      ...transformUserContacts(defaultContacts),
      ...extractOperatorUsersFromUsersInfo(usersInfo ?? {}, decisionNotification?.operators),
    ])
    .create();
}
