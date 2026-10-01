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
        path: 'has-actions',
        title: 'Did the final audit report identify any corrective actions the operator must complete?',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () => import('./has-actions/has-actions.component').then((c) => c.HasActionsComponent),
      },
      {
        path: 'actions',
        title: 'Add corrective actions',
        data: { breadcrumb: false, backlink: '../has-actions' },
        loadComponent: () => import('./actions/corrective-actions.component').then((c) => c.CorrectiveActionsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Summary',
        resolve: {
          hasActions: () =>
            inject(RequestTaskStore).select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
              ?.correctiveActions?.hasActions,
        },
        data: {
          breadcrumb: false,
          backlink: ({ hasActions }: { hasActions: boolean }) => (hasActions ? '../actions' : '../has-actions'),
        },
        loadComponent: () =>
          import('./check-your-answers/corrective-actions-check-your-answers.component').then(
            (c) => c.CorrectiveActionsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/corrective-actions-summary.component').then((c) => c.CorrectiveActionsSummaryComponent),
      },
    ],
  },
];
