export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_TIMELINE_ROUTES = [
  {
    path: '',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./underlying-agreement-variation-regulator-led-submitted-task-list/underlying-agreement-variation-regulator-led-submitted-task-list.component').then(
        (c) => c.UnARegulatorLedVariationSubmittedTaskListComponent,
      ),
  },
  {
    path: 'variation-details',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.VariationDetailsSubmittedComponent),
  },
  {
    path: 'review-target-unit-details',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.ReviewTargetUnitDetailsSubmittedComponent),
  },
  {
    path: 'review-manage-facilities',
    title: 'Manage facilities list',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.ManageFacilitiesSubmittedComponent),
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
            loadComponent: () => import('@requests/common').then((c) => c.FacilitySubmittedComponent),
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
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod5SubmittedComponent),
  },
  {
    path: 'target-period-6',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.TargetPeriod6SubmittedComponent),
  },
  {
    path: 'authorisation-additional-evidence',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.AuthorisationAdditionalEvidenceSubmittedComponent),
  },
  {
    path: 'operator-assent-decision',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('@requests/common').then((c) => c.OperatorAssentDecisionSubmittedComponent),
  },
];
