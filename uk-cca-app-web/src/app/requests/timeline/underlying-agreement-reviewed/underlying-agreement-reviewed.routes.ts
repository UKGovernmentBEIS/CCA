export const UNDERLYING_AGREEMENT_REVIEWED_TIMELINE_ROUTES = [
  {
    path: '',
    loadComponent: () =>
      import('./underlying-agreement-reviewed-task-list/underlying-agreement-reviewed-task-list.component').then(
        (c) => c.UnderlyingAgreementReviewedTaskListComponent,
      ),
    data: { backlink: false },
  },
  {
    path: 'review-target-unit-details',
    loadComponent: () =>
      import('./subtasks/review-target-unit-details/review-target-unit-details.component').then(
        (c) => c.ReviewTargetUnitDetailsComponent,
      ),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    title: 'Manage facilities list',
    path: 'review-manage-facilities',
    loadComponent: () =>
      import('./subtasks/review-manage-facilities/review-manage-facilities.component').then(
        (c) => c.ReviewManageFacilitiesComponent,
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
            loadComponent: () => import('./subtasks/facility/facility.component').then((c) => c.FacilityComponent),
            data: { backlink: '../../review-manage-facilities', breadcrumb: false },
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
    path: 'authorisation-additional-evidence',
    loadComponent: () =>
      import('./subtasks/authorisation-additional-evidence/authorisation-additional-evidence.component').then(
        (c) => c.AuthorisationAdditionalEvidenceComponent,
      ),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'target-period-5',
    loadComponent: () =>
      import('./subtasks/target-period-5/target-period-5.component').then((c) => c.TargetPeriod5Component),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'target-period-6',
    loadComponent: () =>
      import('./subtasks/target-period-6/target-period-6.component').then((c) => c.TargetPeriod6Component),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'overall-decision',
    loadComponent: () =>
      import('./subtasks/overall-decision/overall-decision.component').then((c) => c.OverallDecisionComponent),
    data: { backlink: '../', breadcrumb: false },
  },
];
