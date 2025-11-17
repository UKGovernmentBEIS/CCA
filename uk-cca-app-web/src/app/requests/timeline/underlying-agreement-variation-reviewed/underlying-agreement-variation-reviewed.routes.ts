export const UNDERLYING_AGREEMENT_VARIATION_REVIEWED_TIMELINE_ROUTES = [
  {
    path: '',
    data: { backlink: '../' },
    loadComponent: () =>
      import(
        './underlying-agreement-variation-reviewed-task-list/underlying-agreement-variation-reviewed-task-list.component'
      ).then((c) => c.UnderlyingAgreementVariationReviewedTaskListComponent),
  },
  {
    path: 'variation-details',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/variation-details/variation-details.component').then((c) => c.VariationDetailsComponent),
  },
  {
    path: 'review-target-unit-details',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/review-target-unit-details/review-target-unit-details.component').then(
        (c) => c.ReviewTargetUnitDetailsComponent,
      ),
  },
  {
    path: 'review-manage-facilities',
    title: 'Manage facilities list',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/review-manage-facilities/review-manage-facilities.component').then(
        (c) => c.ReviewManageFacilitiesComponent,
      ),
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
            data: { backlink: '../../review-manage-facilities', breadcrumb: false },
            loadComponent: () => import('./subtasks/facility/facility.component').then((c) => c.FacilityComponent),
          },
          {
            path: 'products',
            title: 'View Products',
            data: { breadcrumb: false, backlink: '../' },
            loadComponent: () => import('@requests/common').then((c) => c.SummaryProductsTimelineComponent),
          },
        ],
      },
    ],
  },
  {
    path: 'target-period-5',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/target-period-5/target-period-5.component').then((c) => c.TargetPeriod5Component),
  },
  {
    path: 'target-period-6',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/target-period-6/target-period-6.component').then((c) => c.TargetPeriod6Component),
  },
  {
    path: 'authorisation-additional-evidence',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/authorisation-additional-evidence/authorisation-additional-evidence.component').then(
        (c) => c.AuthorisationAdditionalEvidenceComponent,
      ),
  },
  {
    path: 'overall-decision',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/overall-decision/overall-decision.component').then((c) => c.OverallDecisionComponent),
  },
];
