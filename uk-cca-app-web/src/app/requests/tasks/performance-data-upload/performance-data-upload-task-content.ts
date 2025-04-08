import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { PerformanceDataUploadProcessComponent } from './process/performance-data-upload-process.component';

export const performanceDataUploadTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Target period reporting (TPR) spreadsheets upload',
    contentComponent: PerformanceDataUploadProcessComponent,
  };
};
