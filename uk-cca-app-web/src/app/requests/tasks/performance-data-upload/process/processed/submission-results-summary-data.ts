import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

export function toSubmissionResultsSummaryData(successfulReportsCount, failedReportsCount, csvFile) {
  const factory = new SummaryFactory();
  factory.addSection('').addRow('Files uploaded', successfulReportsCount).addRow('Files failed', failedReportsCount);

  if (csvFile) {
    factory.addFileListRow('Submission summary file', fileUtils.toDownloadableDocument([csvFile], './file-download'));
  }

  return factory.create();
}
