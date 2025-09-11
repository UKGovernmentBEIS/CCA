import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'target-unit-details',
        title: 'Target unit details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewTargetUnitDetailsComponent),
      },
      {
        path: 'manage-facilities',
        title: 'Manage facilities list',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewManageFacilitiesComponent),
      },
      {
        path: 'facility',
        children: [
          {
            path: ':facilityId',
            title: 'Facility',
            data: { backlink: '../../', breadcrumb: false },
            loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewFacilityComponent),
          },
        ],
      },
      {
        path: 'authorisation-additional-evidence',
        title: 'Authorisation and additional evidence',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('@requests/common').then((c) => c.UNAPeerReviewAuthorisationAdditionalEvidenceComponent),
      },
      {
        path: 'overall-decision',
        title: 'Overall decision',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UNAPeerReviewOverallDecisionComponent),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
