import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'upload-enforcement-response-notice',
        title: 'Enforcement response notice',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UploadEnforcementResponseNoticeSummaryComponent),
      },
      {
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((m) => m.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: 'upload-enforcement-response-notice',
      },
    ],
  },
];
