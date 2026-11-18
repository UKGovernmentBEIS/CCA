import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { UnderlyingAgreementVariationPeerReviewStore } from '../+state/underlying-agreement-variation-peer-review.store';

export const PEER_REVIEW_DECISION_ROUTES: Routes = [
  {
    path: '',
    providers: [UnderlyingAgreementVariationPeerReviewStore],
    canDeactivate: [
      () => {
        inject(UnderlyingAgreementVariationPeerReviewStore).reset();
        return true;
      },
    ],
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review decision', backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./peer-review-decision.component').then((m) => m.PeerReviewDecisionComponent),
      },
      {
        path: 'check-your-answers',
        data: { pageTitle: 'Check your answers', backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('../check-your-answers/check-your-answers.component').then((m) => m.CheckYourAnswersComponent),
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Confirmation', breadcrumb: false },
        loadComponent: () => import('../confirmation/confirmation.component').then((m) => m.ConfirmationComponent),
      },
    ],
  },
];
