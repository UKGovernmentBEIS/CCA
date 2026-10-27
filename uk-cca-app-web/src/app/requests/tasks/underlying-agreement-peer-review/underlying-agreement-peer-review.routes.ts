import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const UNDERLYING_AGREEMENT_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Peer review application for underlying agreement',
    path: '',
    children: [
      {
        title: 'Target unit details',
        path: 'target-unit-details',
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewTargetUnitDetailsComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Manage facilities list',
        path: 'manage-facilities',
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewManageFacilitiesComponent),
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
                loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewFacilityComponent),
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
        title: 'Authorisation and additional evidence',
        path: 'authorisation-additional-evidence',
        loadComponent: () =>
          import('@requests/common').then((c) => c.UNAPeerReviewAuthorisationAdditionalEvidenceComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Overall decision',
        path: 'overall-decision',
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewOverallDecisionComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((r) => r.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
