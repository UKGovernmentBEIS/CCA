import { Routes } from '@angular/router';

import { uploadNoticeOfIntentEditGuard, uploadNoticeOfIntentRedirectGuard } from './upload-notice-of-intent.guard';

export const UPLOAD_NOTICE_OF_INTENT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [uploadNoticeOfIntentRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/notice-of-intent-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/notice-of-intent-check-your-answers.component'),
      },
      {
        path: 'upload-notice',
        title: 'Upload notice',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [uploadNoticeOfIntentEditGuard],
        loadComponent: () => import('./upload-notice/upload-notice.component'),
      },
    ],
  },
];
