import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Underlying agreement variation sent to peer reviewer',
    path: '',
    children: [
      {
        title: 'Variation details',
        path: 'variation-details',
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewVariationDetailsComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Target unit details',
        path: 'review-target-unit-details',
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetUnitDetailsComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Manage facilities list',
        path: 'manage-facilities',
        loadComponent: () => import('@requests/common').then((c) => c.RegulatorLedPeerReviewManageFacilitiesComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: 'facility',
        children: [
          {
            path: ':facilityId',
            children: [
              {
                title: 'Facility',
                path: '',
                loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewFacilityComponent),
                data: { backlink: '../../manage-facilities', breadcrumb: false },
              },
              {
                title: 'View Products',
                path: 'products',
                loadComponent: () => import('@requests/common').then((c) => c.SummaryProductsPeerReviewComponent),
                data: { breadcrumb: false, backlink: '../' },
              },
            ],
          },
        ],
      },
      {
        title: 'TP5 (2021-2022)',
        path: 'target-period-5',
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod5Component),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'TP6 (2024)',
        path: 'target-period-6',
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod6Component),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Authorisation and additional evidence',
        path: 'authorisation-additional-evidence',
        loadComponent: () =>
          import('@requests/common').then((c) => c.UNAVariationPeerReviewAuthorisationAdditionalEvidenceComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Determine operator assent',
        path: 'operator-assent-decision',
        loadComponent: () => import('@requests/common').then((c) => c.OperatorAssentDecisionComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
