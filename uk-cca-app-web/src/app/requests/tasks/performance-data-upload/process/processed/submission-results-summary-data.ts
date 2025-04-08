import { SummaryFactory } from '@shared/components';
import { transformFileInfoToDownloadableFile } from '@shared/utils';

export function toSubmissionResultsSummaryData(successfulReportsCount, failedReportsCount, csvFile) {
  const factory = new SummaryFactory();
  factory.addSection('').addRow('Files uploaded', successfulReportsCount).addRow('Files failed', failedReportsCount);

  if (csvFile) {
    factory.addFileListRow('Submission summary file', transformFileInfoToDownloadableFile(csvFile, './file-download'));
  }

  return factory.create();
}
