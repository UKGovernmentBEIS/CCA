import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementVariationActivatedRequestActionPayload } from 'cca-api';

export function toUnderlyingAgreementVariationActivatedSummaryData(
  payload: UnderlyingAgreementVariationActivatedRequestActionPayload,
) {
  return new SummaryFactory()
    .addSection('Details')
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          payload.underlyingAgreementActivationDetails.evidenceFiles,
          payload.underlyingAgreementActivationAttachments,
        ),
        'file-download',
      ),
    )
    .addRow('Comments', payload.underlyingAgreementActivationDetails.comments)

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
      'Official notices',
      fileUtils.toDownloadableDocument(
        [...Object.values(payload.underlyingAgreementDocuments), ...payload.officialNotices],
        'file-download',
      ),
    )
    .create();
}
