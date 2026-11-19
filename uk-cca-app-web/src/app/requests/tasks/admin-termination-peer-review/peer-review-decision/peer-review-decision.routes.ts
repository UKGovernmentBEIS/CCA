import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AdminTerminationPeerReviewStore } from '../+state';

export const PEER_REVIEW_DECISION_ROUTES: Routes = [
  {
    path: '',
    canDeactivate: [
      () => {
        inject(AdminTerminationPeerReviewStore).reset();
        return true;
      },
    ],
    children: [
      {
        title: 'Peer review decision',
        path: '',
        loadComponent: () => import('./peer-review-decision.component').then((c) => c.PeerReviewDecisionComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('../check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Confirmation',
        path: 'confirmation',
        loadComponent: () => import('../confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        data: { breadcrumb: false },
      },
    ],
    providers: [AdminTerminationPeerReviewStore],
  },
];
