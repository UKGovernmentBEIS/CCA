import { Routes } from '@angular/router';

import { WorkflowDetailsResolver } from './workflow-details.resolver';

export const WORKFLOW_DETAILS_ROUTES: Routes = [
  {
    path: 'workflow-details',
    children: [
      {
        path: ':workflowId',
        resolve: { workflowDetailsItemsAndActions: WorkflowDetailsResolver },
        data: {
          pageTitle: 'Workflow details',
          breadcrumb: {
            resolveText: ({ workflowDetailsItemsAndActions }) => `${workflowDetailsItemsAndActions.workflowDetails.id}`,
          },
        },
        children: [
          {
            path: '',
            loadComponent: () => import('./workflow-details.component').then((c) => c.WorkflowDetailsComponent),
          },
          {
            path: 'timeline',
            loadChildren: () => import('@requests/timeline').then((c) => c.TIMELINE_ROUTES),
          },
        ],
      },
    ],
  },
];
