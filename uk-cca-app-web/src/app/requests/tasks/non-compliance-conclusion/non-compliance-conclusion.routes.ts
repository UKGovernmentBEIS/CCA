import { Routes } from '@angular/router';

import { CLOSE_TASK_ROUTES } from '@requests/common';

import {
  nonComplianceConclusionEditableGuard,
  nonComplianceConclusionRedirectGuard,
} from './non-compliance-conclusion.guard';

export const NON_COMPLIANCE_CONCLUSION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [nonComplianceConclusionRedirectGuard],
        children: [],
      },
      {
        path: 'provide-details',
        title: 'Provide conclusion details',
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
      },
      {
        path: 'provide-withdrawal-notice',
        title: 'Provide withdrawal notice',
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../provide-details', breadcrumb: false },
        loadComponent: () =>
          import('./provide-withdrawal-notice/provide-withdrawal-notice.component').then(
            (c) => c.ProvideWithdrawalNoticeComponent,
          ),
      },
      {
        path: 'provide-appeal-details',
        loadChildren: () =>
          import('./provide-appeal-details/provide-appeal-details.routes').then((r) => r.PROVIDE_APPEAL_DETAILS_ROUTES),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./summary/summary.component').then((c) => c.ConclusionSummaryComponent),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [nonComplianceConclusionEditableGuard],
        loadChildren: () =>
          import('./notify-operator/notify-operator.routes').then(
            (r) => r.NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then((r) => r.NON_COMPLIANCE_CONCLUSION_COMPLETE_TASK_ROUTES),
      },
      ...CLOSE_TASK_ROUTES,
    ],
  },
];
