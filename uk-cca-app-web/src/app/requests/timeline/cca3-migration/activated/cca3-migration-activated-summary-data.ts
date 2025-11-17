import { extractSignatoryUserFromUsersInfo, transformUserContacts } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails,
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
} from 'cca-api';

export function toCca3MigrationActivatedSummaryData(
  activationDetails: Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails,
  activationAttachments: Record<string, string>,
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  underlyingAgreementDocument: FileInfoDTO,
  usersInfo: Record<string, RequestActionUserInfo>,
  decisionNotification: CcaDecisionNotification,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(activationDetails?.evidenceFiles, activationAttachments),
        downloadUrl,
      ),
    )
    .addTextAreaRow('Comments', activationDetails?.comments ?? 'Not provided')

    .addSection('Recipients')
    .addTextAreaRow('Users', [...transformUserContacts(defaultContacts)])
    .addRow(
      'Name and signature of the official notice',
      extractSignatoryUserFromUsersInfo(usersInfo, decisionNotification?.signatory),
    )
    .addFileListRow(
      'Official notice',
      fileUtils.toDownloadableDocument([underlyingAgreementDocument, officialNotice], downloadUrl),
    )
    .create();
}
