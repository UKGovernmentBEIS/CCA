import { Routes } from '@angular/router';

import { CLOSE_TASK_ROUTES, isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const ENFORCEMENT_RESPONSE_NOTICE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'upload-enforcement-response-notice',
        title: 'Upload enforcement response notice',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/upload-enforcement-response-notice/upload-enforcement-response-notice.routes').then(
            (r) => r.UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_ROUTES,
          ),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/enforcement-response-notice-notify-operator.routes').then(
            (r) => r.ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: 'send-for-peer-review',
        title: 'Send for peer review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/enforcement-response-notice-send-for-peer-review.routes').then(
            (r) => r.ENFORCEMENT_RESPONSE_NOTICE_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      ...CLOSE_TASK_ROUTES,
    ],
  },
];
