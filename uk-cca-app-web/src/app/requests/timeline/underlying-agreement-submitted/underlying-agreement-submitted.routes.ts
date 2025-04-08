import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_SUBMITTED_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: false },
    loadComponent: () =>
      import('./underlying-agreement-submitted-task-list/underlying-agreement-submitted-task-list.component'),
  },
  {
    path: 'review-target-unit-details',
    title: 'Review target unit details',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.ReviewTargetUnitDetailsSubmittedComponent),
  },
  {
    path: 'manage-facilities',
    title: 'Manage facilities list',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.ManageFacilitiesSubmittedComponent),
  },
  {
    path: 'facility',
    children: [
      {
        path: ':facilityId',
        title: 'Facility',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.FacilitySubmittedComponent),
      },
    ],
  },
  {
    path: 'target-period-5',
    title: 'TP5 (2021-2022)',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod5SubmittedComponent),
  },
  {
    path: 'target-period-6',
    title: 'TP6 (2024)',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod6SubmittedComponent),
  },
  {
    path: 'authorisation-additional-evidence',
    title: 'Authorisation and additional evidence',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.AuthorisationAdditionalEvidenceSubmittedComponent),
  },
];
