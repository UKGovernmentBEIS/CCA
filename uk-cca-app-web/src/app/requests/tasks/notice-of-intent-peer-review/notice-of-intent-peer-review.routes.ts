import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const NOTICE_OF_INTENT_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Peer review notice of intent',
    path: '',
    children: [
      {
        title: 'Upload notice of intent file',
        path: 'upload-notice-of-intent',
        loadComponent: () => import('@requests/common').then((c) => c.UploadNoticeOfIntentSummaryComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((r) => r.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: 'upload-notice-of-intent',
      },
    ],
  },
];
