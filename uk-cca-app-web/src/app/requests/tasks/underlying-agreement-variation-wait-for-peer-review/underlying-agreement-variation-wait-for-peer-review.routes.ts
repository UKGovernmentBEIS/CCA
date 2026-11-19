import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Application for underlying agreement variation sent to peer reviewer',
    path: '',
    children: [
      {
        title: 'Variation details',
        path: 'variation-details',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/variation-details/variation-details.component').then(
            (c) => c.VariationDetailsComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Target unit details',
        path: 'review-target-unit-details',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/review-target-unit-details/review-target-unit-details.component').then(
            (c) => c.ReviewTargetUnitDetailsComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Manage facilities list',
        path: 'manage-facilities',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/manage-facilities/manage-facilities.component').then(
            (c) => c.ManageFacilitiesComponent,
          ),
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
                loadComponent: () =>
                  import('@requests/common/underlying-agreement-variation-peer-review/facility/facility.component').then(
                    (c) => c.FacilityComponent,
                  ),
                data: { backlink: '../../manage-facilities', breadcrumb: false },
              },
              {
                title: 'View Products',
                path: 'products',
                loadComponent: () =>
                  import('@requests/common/underlying-agreement-peer-review/products/summary-products-peer-review.component').then(
                    (c) => c.SummaryProductsPeerReviewComponent,
                  ),
                data: { breadcrumb: false, backlink: '../' },
              },
            ],
          },
        ],
      },
      {
        title: 'TP5 (2021-2022)',
        path: 'target-period-5',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/target-period-5/target-period-5.component').then(
            (c) => c.TargetPeriod5Component,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'TP6 (2024)',
        path: 'target-period-6',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/target-period-6/target-period-6.component').then(
            (c) => c.TargetPeriod6Component,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Authorisation and additional evidence',
        path: 'authorisation-additional-evidence',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/authorisation-additional-evidence/authorisation-additional-evidence.component').then(
            (c) => c.AuthorisationAdditionalEvidenceComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Overall decision',
        path: 'overall-decision',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-variation-peer-review/overall-decision/overall-decision.component').then(
            (c) => c.OverallDecisionComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
