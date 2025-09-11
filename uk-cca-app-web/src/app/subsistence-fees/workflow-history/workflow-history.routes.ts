import { Routes } from '@angular/router';

import { WorkflowHistoryDetailsResolver } from './workflow-history.resolver';

export const SUBSISTENCE_FEES_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':id',
    resolve: { details: WorkflowHistoryDetailsResolver },
    data: {
      breadcrumb: ({ details }) => ({
        text: `${details.workflowDetails.id}`,
        link: `/subsistence-fees/workflow-history/${details.workflowDetails.id}`,
      }),
    },
    children: [
      {
        path: '',
        loadComponent: () => import('./workflow-history.component').then((c) => c.WorkflowHistoryComponent),
      },
      {
        path: 'timeline',
        loadChildren: () => import('@requests/timeline').then((c) => c.TIMELINE_ROUTES),
      },
    ],
  },
];
