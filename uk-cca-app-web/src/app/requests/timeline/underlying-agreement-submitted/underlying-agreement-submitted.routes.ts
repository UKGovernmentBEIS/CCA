import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_SUBMITTED_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./underlying-agreement-submitted-task-list/underlying-agreement-submitted-task-list.component'),
    data: { backlink: false },
  },
  {
    title: 'Review target unit details',
    path: 'review-target-unit-details',
    loadComponent: () => import('@requests/common').then((c) => c.ReviewTargetUnitDetailsSubmittedComponent),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    title: 'Manage facilities list',
    path: 'manage-facilities',
    loadComponent: () => import('@requests/common').then((c) => c.ManageFacilitiesSubmittedComponent),
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
            pathMatch: 'full',
            loadComponent: () => import('@requests/common').then((c) => c.FacilitySubmittedComponent),
            data: { backlink: '../../manage-facilities', breadcrumb: false },
          },
          {
            title: 'View Products',
            path: 'products',
            loadComponent: () => import('@requests/common').then((c) => c.SummaryProductsTimelineComponent),
            data: { breadcrumb: false, backlink: '../' },
          },
        ],
      },
    ],
  },
  {
    title: 'TP5 (2021-2022)',
    path: 'target-period-5',
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod5SubmittedComponent),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    title: 'TP6 (2024)',
    path: 'target-period-6',
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod6SubmittedComponent),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    title: 'Authorisation and additional evidence',
    path: 'authorisation-additional-evidence',
    loadComponent: () => import('@requests/common').then((c) => c.AuthorisationAdditionalEvidenceSubmittedComponent),
    data: { backlink: '../../', breadcrumb: false },
  },
];
