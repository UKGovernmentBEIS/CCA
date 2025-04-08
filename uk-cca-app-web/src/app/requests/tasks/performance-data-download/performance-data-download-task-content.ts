import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { PerformanceDataDownloadGenerateComponent } from './generate/performance-data-download-generate.component';

export const performanceDataDownloadTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Download target period reporting (TPR) spreadsheets',
    contentComponent: PerformanceDataDownloadGenerateComponent,
  };
};
