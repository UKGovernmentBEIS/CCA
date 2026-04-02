import { Routes } from '@angular/router';

import { nonComplianceDetailsEditableGuard, nonComplianceDetailsRedirectGuard } from './non-compliance-details.guard';

export const NON_COMPLIANCE_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [nonComplianceDetailsRedirectGuard],
        children: [],
      },
      {
        path: 'provide-details',
        title: 'Enter details of non-compliance',
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
      },
      {
        path: 'choose-relevant-workflows',
        title: 'Choose relevant workflows (optional)',
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../provide-details', breadcrumb: false },
        loadComponent: () =>
          import('./choose-relevant-workflows/choose-relevant-workflows.component').then(
            (c) => c.ChooseRelevantWorkflowsComponent,
          ),
      },
      {
        path: 'choose-relevant-facilities',
        title: 'Choose relevant facilities (optional)',
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../choose-relevant-workflows', breadcrumb: false },
        loadComponent: () =>
          import('./choose-relevant-facilities/choose-relevant-facilities.component').then(
            (c) => c.ChooseRelevantFacilitiesComponent,
          ),
      },
      {
        path: 'issue-enforcement',
        title: 'Will you be issuing an Enforcement Response Notice?',
        canActivate: [nonComplianceDetailsEditableGuard],
        data: { backlink: '../choose-relevant-facilities', breadcrumb: false },
        loadComponent: () =>
          import('./issue-enforcement/issue-enforcement.component').then((c) => c.IssueEnforcementComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
      },
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then((r) => r.NON_COMPLIANCE_COMPLETE_TASK_ROUTES),
      },
    ],
  },
];
