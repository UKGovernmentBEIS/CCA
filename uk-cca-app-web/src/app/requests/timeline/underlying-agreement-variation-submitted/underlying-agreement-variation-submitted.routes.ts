import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Variation details',
        path: 'variation-details',
        loadComponent: () =>
          import('@requests/common/timeline-underlying-agreement/variation-details-submitted/variation-details-submitted.component').then(
            (c) => c.VariationDetailsSubmittedComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Review target unit details',
        path: 'review-target-unit-details',
        loadComponent: () =>
          import('@requests/common/timeline-underlying-agreement/review-target-unit-details-submitted/review-target-unit-details-submitted.component').then(
            (c) => c.ReviewTargetUnitDetailsSubmittedComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Manage facilities list',
        path: 'manage-facilities',
        loadComponent: () =>
          import('@requests/common/timeline-underlying-agreement/manage-facilities-submitted/manage-facilities-submitted.component').then(
            (c) => c.ManageFacilitiesSubmittedComponent,
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
                  import('@requests/common/timeline-underlying-agreement/facility-submitted/facility-submitted.component').then(
                    (c) => c.FacilitySubmittedComponent,
                  ),
                data: { backlink: '../../manage-facilities', breadcrumb: false },
              },
              {
                title: 'View Products',
                path: 'products',
                loadComponent: () =>
                  import('@requests/common/timeline-underlying-agreement/products/summary-products-timeline.component').then(
                    (c) => c.SummaryProductsTimelineComponent,
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
          import('@requests/common/timeline-underlying-agreement/target-period-5-submitted/target-period-5-submitted.component').then(
            (c) => c.TargetPeriod5SubmittedComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'TP6 (2024)',
        path: 'target-period-6',
        loadComponent: () =>
          import('@requests/common/timeline-underlying-agreement/target-period-6-submitted/target-period-6-submitted.component').then(
            (c) => c.TargetPeriod6SubmittedComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Authorisation and additional evidence',
        path: 'authorisation-additional-evidence',
        loadComponent: () =>
          import('@requests/common/timeline-underlying-agreement/authorisation-additional-evidence-submitted/authorisation-additional-evidence-submitted.component').then(
            (c) => c.AuthorisationAdditionalEvidenceSubmittedComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
    ],
  },
];
