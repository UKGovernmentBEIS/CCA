import { Routes } from '@angular/router';

import { NOTES_ROUTES, ResolvedWorkflowDetails, WorkflowDetailsResolver } from '@shared/components';

export const SUBSISTENCE_FEES_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':workflowId',
    resolve: { details: WorkflowDetailsResolver },
    data: {
      breadcrumb: ({ details }: { details: ResolvedWorkflowDetails }) => ({
        text: `${details.workflowDetails.id}`,
        link: `/subsistence-fees/workflow-history/${details.workflowDetails.id}`,
      }),
    },
    children: [
      {
        path: '',
        title: 'Workflow history',
        loadComponent: () => import('./workflow-history.component').then((c) => c.WorkflowHistoryComponent),
      },
      {
        path: 'timeline',
        loadChildren: () => import('@requests/timeline').then((c) => c.TIMELINE_ROUTES),
      },
      ...NOTES_ROUTES,
    ],
  },
];
