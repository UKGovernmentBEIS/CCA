import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  UnderlyingAgreementVariationAcceptedRequestActionPayload,
  UnderlyingAgreementVariationRejectedRequestActionPayload,
} from 'cca-api';

export function toAcceptedDecisionDetailsSummaryData(
  payload: UnderlyingAgreementVariationAcceptedRequestActionPayload,
): SummaryData {
  const factory = new SummaryFactory();
  addSummaryDataToFactory(factory, payload);
  return factory.create();
}

export function toRejectedDecisionDetailsSummaryData(
  payload: UnderlyingAgreementVariationRejectedRequestActionPayload,
): SummaryData {
  const factory = new SummaryFactory();
  addSummaryDataToFactory(factory, payload);
  return factory.create();
}

function addSummaryDataToFactory(
  factory: SummaryFactory,
  payload:
    | UnderlyingAgreementVariationAcceptedRequestActionPayload
    | UnderlyingAgreementVariationRejectedRequestActionPayload,
): SummaryFactory {
  const determination = payload.determination;
  const decisionValue = determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  factory
    .addSection('Decision details')
    .addRow('Application', 'Underlying agreement variation', { link: 'underlying-agreement-variation-reviewed' })
    .addRow('Decision', decisionValue);

  if (determination.type === 'REJECTED') {
    factory.addRow('Rejection reason', determination.reason);
  }

  factory
    .addRow('Additional information', determination?.additionalInformation)
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(determination.files, payload.reviewAttachments),
        'file-download',
      ),
    );

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
    );

  if ('underlyingAgreementDocument' in payload) {
    factory.addFileListRow(
      'Official notice',
      fileUtils.toDownloadableDocument([payload.underlyingAgreementDocument, payload.officialNotice], 'file-download'),
    );
  } else {
    factory.addFileListRow(
      'Official notice',
      fileUtils.toDownloadableDocument([payload.officialNotice], 'file-download'),
    );
  }

  return factory;
}
