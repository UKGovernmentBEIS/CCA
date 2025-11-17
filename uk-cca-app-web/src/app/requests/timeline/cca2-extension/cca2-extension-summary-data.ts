import { transformUserContacts } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { DefaultNoticeRecipient, FileInfoDTO } from 'cca-api';

export function toCca2ExtensionSummaryData(
  defaultContacts: DefaultNoticeRecipient[],
  officialNotice: FileInfoDTO,
  underlyingAgreementDocument: FileInfoDTO,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('Recipients')
    .addTextAreaRow('Users', [...transformUserContacts(defaultContacts)])
    .addFileListRow(
      'Official notice',
      fileUtils.toDownloadableDocument([underlyingAgreementDocument, officialNotice], downloadUrl),
    )
    .create();
}
