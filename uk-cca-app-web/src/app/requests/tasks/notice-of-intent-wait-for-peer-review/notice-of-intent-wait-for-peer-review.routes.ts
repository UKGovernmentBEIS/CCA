import { Routes } from '@angular/router';

export const NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'upload-notice-of-intent',
        title: 'Upload notice of intent file',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UploadNoticeOfIntentSummaryComponent),
      },
      {
        path: '**',
        redirectTo: 'upload-notice-of-intent',
      },
    ],
  },
];
