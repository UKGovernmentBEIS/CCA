import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { FileInfoDTO } from 'cca-api';

type MigratedPayloadData = {
  underlyingAgreementDocument: FileInfoDTO;
  underlyingAgreementAttachments: Record<string, string>;
};

export function toUnderlyingAgreementMigratedSummaryData(payload: MigratedPayloadData) {
  return new SummaryFactory()
    .addSection('Details')
    .addRow('Application', 'Underlying agreement application', { link: 'underlying-agreement-submitted' })
    .addFileListRow(
      'Active underlying agreement',
      fileUtils.toDownloadableDocument([payload.underlyingAgreementDocument], 'file-download'),
    )
    .addFileListRow(
      'Attached documents',
      fileUtils.toDownloadableFiles(payload.underlyingAgreementAttachments, 'file-download'),
    )
    .create();
}
