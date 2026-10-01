import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { EnforcementResponseNoticePeerReviewStore } from '../+state';

export const PEER_REVIEW_DECISION_ROUTES: Routes = [
  {
    path: '',
    providers: [EnforcementResponseNoticePeerReviewStore],
    canDeactivate: [
      () => {
        inject(EnforcementResponseNoticePeerReviewStore).reset();
        return true;
      },
    ],
    children: [
      {
        path: '',
        title: 'Peer review decision',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./peer-review-decision.component').then((m) => m.PeerReviewDecisionComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('../check-your-answers/check-your-answers.component').then((m) => m.CheckYourAnswersComponent),
      },
      {
        path: 'confirmation',
        title: 'Confirmation',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('../confirmation/confirmation.component').then((m) => m.ConfirmationComponent),
      },
    ],
  },
];
