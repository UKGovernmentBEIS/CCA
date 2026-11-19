import { Routes } from '@angular/router';

import { CLOSE_TASK_ROUTES, isEditableGuard } from '@requests/common';

export const NOTICE_OF_INTENT_ROUTES: Routes = [
  {
    title: 'Upload notice of intent',
    path: '',
    children: [
      {
        title: 'Upload notice of intent',
        path: 'upload-notice-of-intent',
        loadChildren: () =>
          import('./subtasks/upload-notice-of-intent/upload-notice-of-intent.routes').then(
            (r) => r.UPLOAD_NOTICE_OF_INTENT_ROUTES,
          ),
      },
      {
        title: 'Notify operator of decision',
        path: 'notify-operator',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/notice-of-intent-notify-operator.routes').then(
            (r) => r.NOTICE_OF_INTENT_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        title: 'Send for peer review',
        path: 'send-for-peer-review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/notice-of-intent-send-for-peer-review.routes').then(
            (r) => r.NOTICE_OF_INTENT_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      ...CLOSE_TASK_ROUTES,
    ],
  },
];
