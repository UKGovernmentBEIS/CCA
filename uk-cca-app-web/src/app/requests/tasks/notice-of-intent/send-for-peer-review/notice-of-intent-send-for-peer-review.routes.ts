import { Routes } from '@angular/router';

import { createCandidateAssigneesResolver, isEditableGuard } from '@requests/common';

export const NOTICE_OF_INTENT_SEND_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    resolve: {
      candidateAssignees: createCandidateAssigneesResolver('NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW'),
    },
    children: [
      {
        title: 'Send for peer review',
        path: '',
        loadComponent: () => import('./send-for-peer-review.component'),
        canActivate: [isEditableGuard],
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Peer review request sent',
        path: 'confirmation/:assigneeId',
        loadComponent: () => import('@requests/common').then((c) => c.PeerReviewConfirmationComponent),
        data: {
          backlink: false,
          breadcrumb: false,
          confirmationPrefix: 'Notice of Intent sent to',
          confirmationSuffix: ' for peer review.',
        },
      },
    ],
  },
];
