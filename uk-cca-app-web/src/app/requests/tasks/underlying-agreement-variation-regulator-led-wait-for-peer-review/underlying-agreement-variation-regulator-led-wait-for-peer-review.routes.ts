import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'variation-details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewVariationDetailsComponent),
      },
      {
        path: 'review-target-unit-details',
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
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod5Component),
      },
      {
        path: 'target-period-6',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAVariationPeerReviewTargetPeriod6Component),
      },
      {
        path: 'authorisation-additional-evidence',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('@requests/common').then((c) => c.UNAVariationPeerReviewAuthorisationAdditionalEvidenceComponent),
      },
      {
        path: 'operator-assent-decision',
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
