import { inject } from '@angular/core';

import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';

import { patCsvUploadQuery } from './performance-account-template-csv-upload.selectors';
import { PatCsvUploadProcessComponent } from './process/pat-csv-upload-process.component';

export const patCsvUploadTaskContent: RequestTaskPageContentFactory = () => {
  const processingStatus = inject(RequestTaskStore).select(patCsvUploadQuery.selectProcessingStatus)();

  return {
    header: 'Upload your PAT reporting file',
    contentComponent: PatCsvUploadProcessComponent,
    hideRelatedActions: processingStatus === 'IN_PROGRESS',
  };
};
