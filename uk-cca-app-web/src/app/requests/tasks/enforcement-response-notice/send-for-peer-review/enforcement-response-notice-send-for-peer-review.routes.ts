import { Routes } from '@angular/router';

import { createCandidateAssigneesResolver, isEditableGuard } from '@requests/common';

export const ENFORCEMENT_RESPONSE_NOTICE_SEND_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    resolve: {
      candidateAssignees: createCandidateAssigneesResolver(
        'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW',
      ),
    },
    children: [
      {
        path: '',
        title: 'Send for peer review',
        data: { backlink: '../../', breadcrumb: false },
        canActivate: [isEditableGuard],
        loadComponent: () => import('./send-for-peer-review.component'),
      },
      {
        path: 'confirmation/:assigneeId',
        title: 'Peer review request sent',
        data: {
          backlink: false,
          breadcrumb: false,
          confirmationPrefix: 'Enforcement Response notice sent to',
          confirmationSuffix: ' for peer review.',
        },
        loadComponent: () => import('@requests/common').then((c) => c.PeerReviewConfirmationComponent),
      },
    ],
  },
];
