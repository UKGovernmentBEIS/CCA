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
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/notice-of-intent-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/notice-of-intent-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Upload notice',
        path: 'upload-notice',
        loadComponent: () => import('./upload-notice/upload-notice.component'),
        canActivate: [uploadNoticeOfIntentEditGuard],
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
