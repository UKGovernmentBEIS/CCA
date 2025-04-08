import { SummaryFactory } from '@shared/components';
import {
  Attachments,
  transformAttachmentsToDownloadableFiles,
  transformFileInfoToDownloadableFile,
} from '@shared/utils';

import { FileInfoDTO } from 'cca-api';

type MigratedPayloadData = {
  underlyingAgreementDocument: FileInfoDTO;
  underlyingAgreementAttachments: Attachments;
};

export function toUnderlyingAgreementMigratedSummaryData(payload: MigratedPayloadData) {
  return new SummaryFactory()
    .addSection('Details')
    .addRow('Application', 'Underlying agreement application', { link: 'underlying-agreement-submitted' })
    .addFileListRow(
      'Active underlying agreement',
      transformFileInfoToDownloadableFile(payload.underlyingAgreementDocument ?? [], 'file-download'),
    )
    .addFileListRow(
      'Attached documents',
      transformAttachmentsToDownloadableFiles(payload.underlyingAgreementAttachments, 'file-download'),
    )
    .create();
}
