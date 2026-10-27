import { Routes } from '@angular/router';

import { NOTES_ROUTES, ResolvedWorkflowDetails, WorkflowDetailsResolver } from '@shared/components';

export const SUBSISTENCE_FEES_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':workflowId',
    data: {
      breadcrumb: ({ details }: { details: ResolvedWorkflowDetails }) => `${details.workflowDetails.id}`,
    },
    resolve: { details: WorkflowDetailsResolver },
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
