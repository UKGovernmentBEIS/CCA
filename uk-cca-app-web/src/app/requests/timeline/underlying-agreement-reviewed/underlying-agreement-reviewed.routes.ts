export const UNDERLYING_AGREEMENT_REVIEW_TIMELINE_ROUTES = [
  {
    path: '',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./underlying-agreement-reviewed-task-list/underlying-agreement-reviewed-task-list.component').then(
        (c) => c.UnderlyingAgreementReviewedTaskListComponent,
      ),
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
    path: 'facility',
    children: [
      {
        path: ':facilityId',
        title: 'Facility',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./subtasks/facility/facility.component').then((c) => c.FacilityComponent),
      },
    ],
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
    path: 'overall-decision',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/overall-decision/overall-decision.component').then((c) => c.OverallDecisionComponent),
  },
];
