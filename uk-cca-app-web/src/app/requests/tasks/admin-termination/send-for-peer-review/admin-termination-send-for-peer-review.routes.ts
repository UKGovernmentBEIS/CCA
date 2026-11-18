import { Routes } from '@angular/router';

import { createCandidateAssigneesResolver, isEditableGuard } from '@requests/common';

export const ADMIN_TERMINATION_SEND_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    resolve: {
      candidateAssignees: createCandidateAssigneesResolver('ADMIN_TERMINATION_APPLICATION_PEER_REVIEW'),
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
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.PeerReviewConfirmationComponent),
      },
    ],
  },
];
