import { DatePipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload } from 'cca-api';

export function toPATReportingSummaryData(
  payload: FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload,
) {
  const datePipe = new DatePipe('en-GB');

  const details = payload?.details;
  const results = payload?.results;
  const uploadAttachments = payload?.uploadAttachments;

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Reporting year', details?.targetYear.toString())
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(details?.files, uploadAttachments), './file-download'),
    )

    .addSection('Submission results')
    .addRow('Time submitted', datePipe.transform(results?.submittedDate, 'd MMM y - H:mm:ss'))
    .addRow('Files uploaded', String(results?.totalFilesUploaded ?? ''))
    .addRow('Facilities successful', String(results?.facilitiesSucceeded ?? ''))
    .addRow('Facilities failed', String(results?.facilitiesFailed ?? ''))
    .addFileListRow(
      'Submission summary file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([results?.uploadSummaryFile], uploadAttachments),
        './file-download',
      ),
    )
    .create();
}
