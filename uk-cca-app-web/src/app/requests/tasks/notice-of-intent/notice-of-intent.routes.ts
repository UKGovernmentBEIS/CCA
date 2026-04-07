import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const NOTICE_OF_INTENT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'upload-notice-of-intent',
        title: 'Upload notice of intent',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/upload-notice-of-intent/upload-notice-of-intent.routes').then(
            (r) => r.UPLOAD_NOTICE_OF_INTENT_ROUTES,
          ),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/notice-of-intent-notify-operator.routes').then(
            (r) => r.NOTICE_OF_INTENT_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: 'send-for-peer-review',
        title: 'Send for peer review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/notice-of-intent-send-for-peer-review.routes').then(
            (r) => r.NOTICE_OF_INTENT_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
    ],
  },
];
