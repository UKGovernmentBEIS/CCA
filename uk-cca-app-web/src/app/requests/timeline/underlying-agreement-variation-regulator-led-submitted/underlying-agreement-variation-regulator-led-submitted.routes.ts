export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_TIMELINE_ROUTES = [
  {
    path: '',
    loadComponent: () =>
      import('./underlying-agreement-variation-regulator-led-submitted-task-list/underlying-agreement-variation-regulator-led-submitted-task-list.component').then(
        (c) => c.UnARegulatorLedVariationSubmittedTaskListComponent,
      ),
    data: { backlink: false },
  },
  {
    path: 'variation-details',
    loadComponent: () => import('@requests/common').then((c) => c.VariationDetailsSubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'review-target-unit-details',
    loadComponent: () => import('@requests/common').then((c) => c.ReviewTargetUnitDetailsSubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    title: 'Manage facilities list',
    path: 'review-manage-facilities',
    loadComponent: () => import('@requests/common').then((c) => c.ManageFacilitiesSubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
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
            loadComponent: () => import('@requests/common').then((c) => c.FacilitySubmittedComponent),
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
    path: 'target-period-5',
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod5SubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'target-period-6',
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod6SubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'authorisation-additional-evidence',
    loadComponent: () => import('@requests/common').then((c) => c.AuthorisationAdditionalEvidenceSubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    path: 'operator-assent-decision',
    loadComponent: () => import('@requests/common').then((c) => c.OperatorAssentDecisionSubmittedComponent),
    data: { backlink: '../', breadcrumb: false },
  },
];
