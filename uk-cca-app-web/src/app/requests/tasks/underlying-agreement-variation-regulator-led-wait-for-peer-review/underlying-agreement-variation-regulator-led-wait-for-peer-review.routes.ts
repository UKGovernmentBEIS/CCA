import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    title: 'Underlying agreement variation sent to peer reviewer',
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
        loadComponent: () => import('@requests/common').then((c) => c.RegulatorLedPeerReviewManageFacilitiesComponent),
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
        path: 'operator-assent-decision',
        title: 'Determine operator assent',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.OperatorAssentDecisionComponent),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
