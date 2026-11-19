import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { auditDetailsCorrectiveActionsQuery } from '../../audit-details-corrective-actions.selectors';
import { correctiveActionsRedirectGuard } from './corrective-actions.guard';

export const CORRECTIVE_ACTIONS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [correctiveActionsRedirectGuard],
        children: [],
      },
      {
        title: 'Did the final audit report identify any corrective actions the operator must complete?',
        path: 'has-actions',
        loadComponent: () => import('./has-actions/has-actions.component').then((c) => c.HasActionsComponent),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Add corrective actions',
        path: 'actions',
        loadComponent: () => import('./actions/corrective-actions.component').then((c) => c.CorrectiveActionsComponent),
        data: { breadcrumb: false, backlink: '../has-actions' },
      },
      {
        title: 'Summary',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/corrective-actions-check-your-answers.component').then(
            (c) => c.CorrectiveActionsCheckYourAnswersComponent,
          ),
        data: {
          breadcrumb: false,
          backlink: ({ hasActions }: { hasActions: boolean }) => (hasActions ? '../actions' : '../has-actions'),
        },
        resolve: {
          hasActions: () =>
            inject(RequestTaskStore).select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
              ?.correctiveActions?.hasActions,
        },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/corrective-actions-summary.component').then((c) => c.CorrectiveActionsSummaryComponent),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];
