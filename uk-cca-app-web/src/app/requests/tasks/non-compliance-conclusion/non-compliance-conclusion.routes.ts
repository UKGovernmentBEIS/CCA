import { Routes } from '@angular/router';

import { CLOSE_TASK_ROUTES } from '@requests/common';

import {
  nonComplianceConclusionEditableGuard,
  nonComplianceConclusionRedirectGuard,
} from './non-compliance-conclusion.guard';

export const NON_COMPLIANCE_CONCLUSION_ROUTES: Routes = [
  {
    title: 'Provide non-compliance conclusion',
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [nonComplianceConclusionRedirectGuard],
        children: [],
      },
      {
        title: 'Provide conclusion details',
        path: 'provide-details',
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Provide withdrawal notice',
        path: 'provide-withdrawal-notice',
        loadComponent: () =>
          import('./provide-withdrawal-notice/provide-withdrawal-notice.component').then(
            (c) => c.ProvideWithdrawalNoticeComponent,
          ),
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../provide-details', breadcrumb: false },
      },
      {
        path: 'provide-appeal-details',
        loadChildren: () =>
          import('./provide-appeal-details/provide-appeal-details.routes').then((r) => r.PROVIDE_APPEAL_DETAILS_ROUTES),
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
        loadComponent: () => import('./summary/summary.component').then((c) => c.ConclusionSummaryComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Notify operator of decision',
        path: 'notify-operator',
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
