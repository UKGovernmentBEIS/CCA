import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Application for underlying agreement sent to peer reviewer',
    path: '',
    children: [
      {
        title: 'Target unit details',
        path: 'target-unit-details',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-peer-review/review-target-unit-details/review-target-unit-details.component').then(
            (c) => c.ReviewTargetUnitDetailsComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Manage facilities list',
        path: 'manage-facilities',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-peer-review/manage-facilities/manage-facilities.component').then(
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
                  import('@requests/common/underlying-agreement-peer-review/facility/facility.component').then(
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
        title: 'Authorisation and additional evidence',
        path: 'authorisation-additional-evidence',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-peer-review/authorisation-additional-evidence/authorisation-additional-evidence.component').then(
            (c) => c.AuthorisationAdditionalEvidenceComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Overall decision',
        path: 'overall-decision',
        loadComponent: () =>
          import('@requests/common/underlying-agreement-peer-review/overall-decision/overall-decision.component').then(
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
