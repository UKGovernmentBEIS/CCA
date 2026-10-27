import { Routes } from '@angular/router';

import { NOTES_ROUTES } from '@shared/components';

import { WorkflowHistoryDetailsResolver, WorkflowHistoryDetailsResponse } from './workflow-history.resolver';

export const BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':workflowId',
    data: {
      breadcrumb: ({ details }: { details: WorkflowHistoryDetailsResponse }) => `${details.workflowDetails.id}`,
    },
    resolve: { details: WorkflowHistoryDetailsResolver },
    children: [
      {
        title: 'Workflow history',
        path: '',
        loadComponent: () => import('./workflow-history.component').then((c) => c.WorkflowHistoryComponent),
      },
      {
        path: 'timeline',
        loadChildren: () => import('@requests/timeline').then((r) => r.TIMELINE_ROUTES),
      },
      ...NOTES_ROUTES,
    ],
  },
];
