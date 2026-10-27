import { DatePipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { FacilityPerformanceAccountTemplateDataUploadResults } from 'cca-api';

export interface PatCsvSubmissionResultsInput {
  results: FacilityPerformanceAccountTemplateDataUploadResults;
  attachments: Record<string, string>;
}

export function toSubmissionResultsSummaryData(input: PatCsvSubmissionResultsInput) {
  const datePipe = new DatePipe('en-GB');

  const factory = new SummaryFactory()
    .addSection('')
    .addRow('Time submitted', datePipe.transform(input.results?.submittedDate, 'd MMM y - H:mm:ss'))
    .addRow('Files uploaded', String(input.results?.totalFilesUploaded ?? ''))
    .addRow('Facilities successful', String(input.results?.facilitiesSucceeded ?? ''))
    .addRow('Facilities failed', String(input.results?.facilitiesFailed ?? ''));

  if (input.results?.uploadSummaryFile) {
    factory.addFileListRow(
      'Submission summary file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([input.results?.uploadSummaryFile], input.attachments),
        '../../file-download',
      ),
    );
  }

  return factory.create();
}
