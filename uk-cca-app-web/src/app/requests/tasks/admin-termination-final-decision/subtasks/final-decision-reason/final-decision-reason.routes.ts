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
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../..', breadcrumb: false },
        loadComponent: () => import('./summary/final-decision-reason-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../..', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/final-decision-reason-check-your-answers.component'),
      },
      {
        path: 'actions',
        title: 'Admin termination final decision actions',
        data: { backlink: '../../..', breadcrumb: false },
        loadComponent: () => import('./actions/final-decision-reason-actions.component'),
      },
      {
        path: 'reason-details',
        title: 'Admin termination final decision reason details',
        data: { backlink: '../actions', breadcrumb: false },
        loadComponent: () => import('./reason-details/final-decision-reason-details.component'),
      },
    ],
  },
];
