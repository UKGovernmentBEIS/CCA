import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { PerformanceDataUploadProcessComponent } from './process/performance-data-upload-process.component';

export const performanceDataUploadTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'TP reporting - Upload spreadsheets',
    contentComponent: PerformanceDataUploadProcessComponent,
  };
};
