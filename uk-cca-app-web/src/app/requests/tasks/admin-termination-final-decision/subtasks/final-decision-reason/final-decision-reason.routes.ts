import { Routes } from '@angular/router';

import { adminTerminationFinalDecisionRedirectGuard } from './final-decision-reason.guard';

export const FINAL_DECISION_REASON_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [adminTerminationFinalDecisionRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/final-decision-reason-summary.component'),
        data: { backlink: '../../..', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/final-decision-reason-check-your-answers.component'),
        data: { backlink: '../../..', breadcrumb: false },
      },
      {
        title: 'Admin termination final decision actions',
        path: 'actions',
        loadComponent: () => import('./actions/final-decision-reason-actions.component'),
        data: { backlink: '../../..', breadcrumb: false },
      },
      {
        title: 'Admin termination final decision reason details',
        path: 'reason-details',
        loadComponent: () => import('./reason-details/final-decision-reason-details.component'),
        data: { backlink: '../actions', breadcrumb: false },
      },
    ],
  },
];
