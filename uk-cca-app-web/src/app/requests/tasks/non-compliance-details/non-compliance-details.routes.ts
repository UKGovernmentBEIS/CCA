import { Routes } from '@angular/router';

import { nonComplianceDetailsEditableGuard, nonComplianceDetailsRedirectGuard } from './non-compliance-details.guard';

export const NON_COMPLIANCE_DETAILS_ROUTES: Routes = [
  {
    title: 'Provide non-compliance details',
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [nonComplianceDetailsRedirectGuard],
        children: [],
      },
      {
        title: 'Enter details of non-compliance',
        path: 'provide-details',
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Choose relevant workflows (optional)',
        path: 'choose-relevant-workflows',
        loadComponent: () =>
          import('./choose-relevant-workflows/choose-relevant-workflows.component').then(
            (c) => c.ChooseRelevantWorkflowsComponent,
          ),
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../provide-details', breadcrumb: false },
      },
      {
        title: 'Choose relevant facilities (optional)',
        path: 'choose-relevant-facilities',
        loadComponent: () =>
          import('./choose-relevant-facilities/choose-relevant-facilities.component').then(
            (c) => c.ChooseRelevantFacilitiesComponent,
          ),
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../choose-relevant-workflows', breadcrumb: false },
      },
      {
        title: 'Will you be issuing an Enforcement Response Notice?',
        path: 'issue-enforcement',
        loadComponent: () =>
          import('./issue-enforcement/issue-enforcement.component').then((c) => c.IssueEnforcementComponent),
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../choose-relevant-facilities', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('@requests/common').then((c) => c.NonComplianceSummaryComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then((r) => r.NON_COMPLIANCE_COMPLETE_TASK_ROUTES),
      },
    ],
  },
];
