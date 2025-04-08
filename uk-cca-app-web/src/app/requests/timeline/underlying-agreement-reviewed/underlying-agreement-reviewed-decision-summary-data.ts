import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import {
  transformAttachmentsAndFileUUIDsToDownloadableFiles,
  transformFileInfoToDownloadableFile,
} from '@shared/utils';

import {
  UnderlyingAgreementAcceptedRequestActionPayload,
  UnderlyingAgreementRejectedRequestActionPayload,
} from 'cca-api';

export function toAcceptedDecisionDetailsSummaryData(
  payload: UnderlyingAgreementAcceptedRequestActionPayload,
): SummaryData {
  const factory = new SummaryFactory();
  addSummaryDataToFactory(factory, payload);
  return factory.create();
}

export function toRejectedDecisionDetailsSummaryData(
  payload: UnderlyingAgreementRejectedRequestActionPayload,
): SummaryData {
  const factory = new SummaryFactory();
  addSummaryDataToFactory(factory, payload);
  return factory.create();
}

function addSummaryDataToFactory(
  factory: SummaryFactory,
  payload: UnderlyingAgreementAcceptedRequestActionPayload | UnderlyingAgreementRejectedRequestActionPayload,
): SummaryFactory {
  const determination = payload.determination;
  const decisionValue = determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';
  factory
    .addSection('Decision details')
    .addRow('Application', 'Underlying agreement application', { link: 'underlying-agreement-reviewed' })

    .addRow('Decision', decisionValue);

  if (determination.type === 'REJECTED') {
    factory.addRow('Rejection reason', determination.reason);
  }

  factory
    .addRow('Additional information', determination?.additionalInformation)
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        determination.files,
        payload.reviewAttachments,
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
      transformFileInfoToDownloadableFile(
        [payload.underlyingAgreementDocument, payload.officialNotice],
        'file-download',
      ),
    );
  } else {
    factory.addFileListRow(
      'Official notice',
      transformFileInfoToDownloadableFile(payload.officialNotice, 'file-download'),
    );
  }

  return factory;
}
