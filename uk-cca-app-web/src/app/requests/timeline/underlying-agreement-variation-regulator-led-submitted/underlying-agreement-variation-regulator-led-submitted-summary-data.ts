import {
  extractOperatorUsersFromUsersInfo,
  extractSectorUsersFromUsersInfo,
  extractSignatoryUserFromUsersInfo,
  transformUserContacts,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload } from 'cca-api';

export function toUnARegulatorLedVariationSubmittedSummaryData(
  payload: UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload,
): SummaryData {
  const factory = new SummaryFactory();

  factory.addSection('Variation details').addRow('Application', 'Underlying agreement variation', {
    link: 'underlying-agreement-variation-regulator-led-submitted',
  });

  factory
    .addSection('Recipients')
    .addRow('Users', [
      ...transformUserContacts(payload?.defaultContacts),
      ...extractSectorUsersFromUsersInfo(payload?.usersInfo, payload?.decisionNotification.sectorUsers),
      ...extractOperatorUsersFromUsersInfo(payload?.usersInfo, payload?.decisionNotification.operators),
    ])
    .addRow(
      'Name and signature on the official notice',
      extractSignatoryUserFromUsersInfo(payload?.usersInfo, payload?.decisionNotification.signatory),
    );

  factory.addFileListRow(
    'Official notice',
    fileUtils.toDownloadableDocument(payload?.officialNotices, 'file-download'),
  );

  return factory.create();
}
