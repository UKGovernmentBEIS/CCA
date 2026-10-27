import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Peer review enforcement response notice',
    path: '',
    children: [
      {
        title: 'Enforcement response notice',
        path: 'upload-enforcement-response-notice',
        loadComponent: () => import('@requests/common').then((c) => c.UploadEnforcementResponseNoticeSummaryComponent),
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
        redirectTo: 'upload-enforcement-response-notice',
      },
    ],
  },
];
