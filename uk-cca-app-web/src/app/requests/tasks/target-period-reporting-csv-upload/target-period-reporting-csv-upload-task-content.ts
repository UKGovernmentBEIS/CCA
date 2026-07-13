import { inject } from '@angular/core';

import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';

import { TprCsvUploadProcessComponent } from './process/tpr-csv-upload-process.component';
import { tprCSVUploadQuery } from './target-period-reporting-csv-upload.selectors';

export const tprCSVUploadTaskContent: RequestTaskPageContentFactory = () => {
  const processingStatus = inject(RequestTaskStore).select(tprCSVUploadQuery.selectProcessingStatus)();

  return {
    header: 'TP reporting - Upload CSV file',
    contentComponent: TprCsvUploadProcessComponent,
    hideRelatedActions: processingStatus === 'IN_PROGRESS',
  };
};
