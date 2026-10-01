import { Routes } from '@angular/router';

import { NOTES_ROUTES } from '@shared/components';

import { WorkflowHistoryDetailsResolver, WorkflowHistoryDetailsResponse } from './workflow-history.resolver';

export const BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':workflowId',
    resolve: { details: WorkflowHistoryDetailsResolver },
    data: {
      breadcrumb: ({ details }: { details: WorkflowHistoryDetailsResponse }) => ({
        text: `${details.workflowDetails.id}`,
        link: `/buyout-surplus/workflow-history/${details.workflowDetails.id}`,
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
