import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  AdminTerminationWithdrawReasonDetails,
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
} from 'cca-api';

export function toAdminTerminationWithdrawSubmittedTimelineSummaryData(
  adminTerminationWithdrawReasonDetails: AdminTerminationWithdrawReasonDetails,
  adminTerminationWithdrawAttachments: Record<string, string>,
  decisionNotification: CcaDecisionNotification,
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  downloadUrl: string,
  usersInfo?: Record<string, RequestActionUserInfo>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addTextAreaRow(
      'Explain why you are withdrawing the admin termination',
      adminTerminationWithdrawReasonDetails.explanation,
    )
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          adminTerminationWithdrawReasonDetails.relevantFiles,
          adminTerminationWithdrawAttachments,
        ),
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
