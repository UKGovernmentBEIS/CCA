import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { FileInfoDTO, FileReports } from 'cca-api';

export function toSubmissionResultsSummaryData(submissionDate: string, fileReports: FileReports, csvFile: FileInfoDTO) {
  const datePipe = new GovukDatePipe();
  const factory = new SummaryFactory();

  factory
    .addSection('')
    .addRow('Time submitted', datePipe.transform(submissionDate, 'datetime'))
    .addRow('Files uploaded', fileReports?.numberOfFilesSucceeded.toString())
    .addRow('Files failed', fileReports?.numberOfFilesFailed.toString());

  if (csvFile) {
    factory.addFileListRow('Submission summary file', fileUtils.toDownloadableDocument([csvFile], './file-download'));
  }

  return factory.create();
}
