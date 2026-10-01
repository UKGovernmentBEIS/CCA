import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

import { UnderlyingAgreementVariationPeerReviewStore } from './+state/underlying-agreement-variation-peer-review.store';

export const UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    title: 'Peer review application for underlying agreement variation',
    providers: [UnderlyingAgreementVariationPeerReviewStore],
    children: [
      {
        path: 'variation-details',
        title: 'Variation details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewVariationDetailsComponent),
      },
      {
        path: 'review-target-unit-details',
        title: 'Target unit details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetUnitDetailsComponent),
      },
      {
        path: 'manage-facilities',
        title: 'Manage facilities list',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewManageFacilitiesComponent),
      },
      {
        path: 'facility',
        children: [
          {
            path: ':facilityId',
            children: [
              {
                path: '',
                title: 'Facility',
                data: { backlink: '../../manage-facilities', breadcrumb: false },
                loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewFacilityComponent),
              },
              {
                path: 'products',
                title: 'View Products',
                data: { breadcrumb: false, backlink: '../' },
                loadComponent: () => import('@requests/common').then((c) => c.SummaryProductsPeerReviewComponent),
              },
            ],
          },
        ],
      },
      {
        path: 'target-period-5',
        title: 'TP5 (2021-2022)',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod5Component),
      },
      {
        path: 'target-period-6',
        title: 'TP6 (2024)',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod6Component),
      },
      {
        path: 'authorisation-additional-evidence',
        title: 'Authorisation and additional evidence',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('@requests/common').then((c) => c.UNAVariationPeerReviewAuthorisationAdditionalEvidenceComponent),
      },
      {
        path: 'overall-decision',
        title: 'Overall decision',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewOverallDecisionComponent),
      },
      {
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((m) => m.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
