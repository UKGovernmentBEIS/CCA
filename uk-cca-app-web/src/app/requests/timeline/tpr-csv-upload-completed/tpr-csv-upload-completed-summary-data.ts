import { DatePipe, TitleCasePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { PerformanceDataFacilityDataUploadCompletedRequestActionPayload } from 'cca-api';

export function toTPRCSVUploadCompletedSummary(
  payload: PerformanceDataFacilityDataUploadCompletedRequestActionPayload,
): SummaryData {
  const titleCasePipe = new TitleCasePipe();
  const datePipe = new DatePipe('en-GB');

  const performanceDataUpload = payload?.performanceDataUpload;
  const results = payload?.results;
  const uploadAttachments = payload?.uploadAttachments;

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Reporting period', performanceDataUpload?.targetPeriodType)
    .addRow('Report type', titleCasePipe.transform(performanceDataUpload?.reportType) ?? '')
    .addTextAreaRow(
      'Uploaded files',
      fileUtils.toFiles(performanceDataUpload?.files ?? [], uploadAttachments ?? {}).map((f) => f.file.name),
    )

    .addSection('Confirmed results')
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
