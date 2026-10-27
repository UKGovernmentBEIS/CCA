import { Routes } from '@angular/router';

import { createCandidateAssigneesResolver, isEditableGuard } from '@requests/common';

export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SEND_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    resolve: {
      candidateAssignees: createCandidateAssigneesResolver(
        'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
      ),
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
        data: { backlink: false, breadcrumb: false },
      },
    ],
  },
];
