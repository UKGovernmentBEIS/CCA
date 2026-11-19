import { Routes } from '@angular/router';

export const NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Notice of intent sent for peer review',
    path: '',
    children: [
      {
        title: 'Upload notice of intent file',
        path: 'upload-notice-of-intent',
        loadComponent: () =>
          import('@requests/common/notice-of-intent-peer-review/upload-notice-of-intent/upload-notice-of-intent-summary.component').then(
            (c) => c.UploadNoticeOfIntentSummaryComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: 'upload-notice-of-intent',
      },
    ],
  },
];
