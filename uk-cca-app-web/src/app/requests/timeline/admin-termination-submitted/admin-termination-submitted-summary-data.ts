import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformAdminTerminationReason,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  AdminTerminationReasonDetails,
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
} from 'cca-api';

export function toAdminTerminationReasonSubmittedTimelineSummaryData(
  adminTerminationReasonDetails: AdminTerminationReasonDetails,
  adminTerminationSubmitAttachments: Record<string, string>,
  decisionNotification: CcaDecisionNotification,
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  downloadUrl: string,
  usersInfo?: Record<string, RequestActionUserInfo>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addTextAreaRow('Termination reason', transformAdminTerminationReason(adminTerminationReasonDetails.reason))
    .addRow('Explain why the account is being terminated', adminTerminationReasonDetails.explanation)
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(adminTerminationReasonDetails.relevantFiles, adminTerminationSubmitAttachments),
        downloadUrl,
      ),
    )

    .addSection('Official notice recipients')
    .addTextAreaRow('Users', [
      ...transformUserContacts(defaultContacts),
      ...extractSectorUsersFromUsersInfo(usersInfo, decisionNotification.sectorUsers),
      ...extractOperatorUsersFromUsersInfo(usersInfo, decisionNotification.operators),
    ])
    .addRow(
      'Name and signature on the official notice',
      extractSignatoryUserFromUsersInfo(usersInfo, decisionNotification.signatory),
    )
    .addFileListRow('Official notice', fileUtils.toDownloadableDocument([officialNotice], downloadUrl))
    .create();
}
