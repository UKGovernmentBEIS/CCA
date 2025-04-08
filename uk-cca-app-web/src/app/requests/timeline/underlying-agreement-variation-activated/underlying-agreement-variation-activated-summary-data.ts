import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryFactory } from '@shared/components';
import {
  transformAttachmentsAndFileUUIDsToDownloadableFiles,
  transformFileInfoToDownloadableFile,
} from '@shared/utils';

import { UnderlyingAgreementVariationActivatedRequestActionPayload } from 'cca-api';

export function toUnderlyingAgreementVariationActivatedSummaryData(
  payload: UnderlyingAgreementVariationActivatedRequestActionPayload,
) {
  const factory = new SummaryFactory();
  factory
    .addSection('Details')
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        payload.underlyingAgreementActivationDetails.evidenceFiles,
        payload.underlyingAgreementActivationAttachments,
        'file-download',
      ),
    )
    .addRow('Comments', payload.underlyingAgreementActivationDetails.comments);

  factory
    .addSection('Recipients')
    .addRow('Users', [
      ...transformUserContacts(payload.defaultContacts),
      ...extractSectorUsersFromUsersInfo(payload.usersInfo, payload.decisionNotification.sectorUsers),
      ...extractOperatorUsersFromUsersInfo(payload.usersInfo, payload.decisionNotification.operators),
    ])
    .addRow(
      'Name and signature on the official notice',
      extractSignatoryUserFromUsersInfo(payload.usersInfo, payload.decisionNotification.signatory),
    )
    .addFileListRow(
      'Official notice',
      transformFileInfoToDownloadableFile(
        [payload.underlyingAgreementDocument, payload.officialNotice],
        'file-download',
      ),
    );
  return factory.create();
}
