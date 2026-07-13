import { DatePipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { PerformanceDataFacilityUploadResults } from 'cca-api';

export function toSubmissionResultsSummaryData(
  results: PerformanceDataFacilityUploadResults,
  attachments: Record<string, string>,
) {
  const datePipe = new DatePipe('en-GB');

  return new SummaryFactory()
    .addSection('')
    .addRow('Time submitted', datePipe.transform(results?.submittedDate, 'd MMM y - H:mm:ss'))
    .addRow('Files uploaded', String(results?.totalFilesUploaded ?? ''))
    .addRow('Facilities successful', String(results?.facilitiesSucceeded ?? ''))
    .addRow('Facilities failed', String(results?.facilitiesFailed ?? ''))
    .addFileListRow(
      'Submission summary file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([results?.uploadSummaryFile], attachments),
        '../../file-download',
      ),
    )
    .create();
}
