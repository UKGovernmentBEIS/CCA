import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsToDownloadableFiles, transformFileInfoToDownloadableFile } from '@shared/utils';

import {
  AdminTerminationFinalDecisionReasonDetails,
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
} from 'cca-api';

export function toAdminTerminationFinalDecisionSubmittedTimelineSummaryData(
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
  adminTerminationFinalDecisionAttachments: { [key: string]: string },
  decisionNotification: CcaDecisionNotification,
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  downloadUrl: string,
  usersInfo?: { [key: string]: RequestActionUserInfo },
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addRow(
      'Decision',
      adminTerminationFinalDecisionReasonDetails.finalDecisionType === 'TERMINATE_AGREEMENT'
        ? 'Terminate agreement'
        : 'Withdraw termination',
      {
        prewrap: true,
      },
    )
    .addRow('Explain reason', adminTerminationFinalDecisionReasonDetails.explanation, {
      prewrap: true,
    })
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsToDownloadableFiles(
        adminTerminationFinalDecisionReasonDetails.relevantFiles,
        adminTerminationFinalDecisionAttachments,
        downloadUrl,
      ),
    )
    .addSection('Official notice recipients')
    .addRow(
      'Users',
      [
        ...transformUserContacts(defaultContacts),
        ...extractSectorUsersFromUsersInfo(usersInfo, decisionNotification.sectorUsers),
        ...extractOperatorUsersFromUsersInfo(usersInfo, decisionNotification.operators),
      ],
      { prewrap: true },
    )
    .addRow(
      'Name and signature on the official notice',
      extractSignatoryUserFromUsersInfo(usersInfo, decisionNotification.signatory),
    )
    .addFileListRow('Official notice', transformFileInfoToDownloadableFile(officialNotice, downloadUrl))
    .create();
}
