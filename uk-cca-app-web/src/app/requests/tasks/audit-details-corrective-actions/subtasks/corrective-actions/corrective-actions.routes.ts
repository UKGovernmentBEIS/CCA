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
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () => import('./has-actions/has-actions.component').then((c) => c.HasActionsComponent),
      },
      {
        path: 'actions',
        data: { breadcrumb: false, backlink: '../has-actions' },
        loadComponent: () => import('./actions/corrective-actions.component').then((c) => c.CorrectiveActionsComponent),
      },
      {
        path: 'check-your-answers',
        resolve: {
          hasActions: () =>
            inject(RequestTaskStore).select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
              ?.correctiveActions?.hasActions,
        },
        data: { breadcrumb: false, backlink: ({ hasActions }) => (hasActions ? '../actions' : '../has-actions') },
        loadComponent: () =>
          import('./check-your-answers/corrective-actions-check-your-answers.component').then(
            (c) => c.CorrectiveActionsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/corrective-actions-summary.component').then((c) => c.CorrectiveActionsSummaryComponent),
      },
    ],
  },
];
