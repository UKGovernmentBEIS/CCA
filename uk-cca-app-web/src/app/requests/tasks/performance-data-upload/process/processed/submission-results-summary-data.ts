import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { FileInfoDTO } from 'cca-api';

export function toSubmissionResultsSummaryData(
  successfulReportsCount: number,
  failedReportsCount: number,
  csvFile: FileInfoDTO,
) {
  const factory = new SummaryFactory();

  factory
    .addSection('')
    .addRow('Files uploaded', successfulReportsCount?.toString())
    .addRow('Files failed', failedReportsCount?.toString());

  if (csvFile) {
    factory.addFileListRow('Submission summary file', fileUtils.toDownloadableDocument([csvFile], './file-download'));
  }

  return factory.create();
}
