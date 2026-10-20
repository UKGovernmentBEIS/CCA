import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const NOTICE_OF_INTENT_PEER_REVIEW_ROUTES: Routes = [
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
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((m) => m.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: 'upload-notice-of-intent',
      },
    ],
  },
];
