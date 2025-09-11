import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  AdminTerminationFinalDecisionReasonDetails,
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
} from 'cca-api';

export function toAdminTerminationFinalDecisionSubmittedTimelineSummaryData(
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
  adminTerminationFinalDecisionAttachments: Record<string, string>,
  decisionNotification: CcaDecisionNotification,
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  downloadUrl: string,
  usersInfo?: Record<string, RequestActionUserInfo>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addTextAreaRow(
      'Decision',
      adminTerminationFinalDecisionReasonDetails.finalDecisionType === 'TERMINATE_AGREEMENT'
        ? 'Terminate agreement'
        : 'Withdraw termination',
    )
    .addTextAreaRow('Explain reason', adminTerminationFinalDecisionReasonDetails.explanation)
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          adminTerminationFinalDecisionReasonDetails.relevantFiles,
          adminTerminationFinalDecisionAttachments,
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
