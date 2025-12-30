import { Routes } from '@angular/router';

import { WORKFLOW_NOTES_ROUTES } from '../../sectors/sector/workflow-history-tab/workflow-details/notes-tab/notes.routes';
import { WorkflowHistoryDetailsResolver } from './workflow-history.resolver';

export const BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES: Routes = [
  {
    path: ':workflowId',
    resolve: { details: WorkflowHistoryDetailsResolver },
    data: {
      breadcrumb: ({ details }) => ({
        text: `${details.workflowDetails.id}`,
        link: `/buyout-surplus/workflow-history/${details.workflowDetails.id}`,
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
      ...WORKFLOW_NOTES_ROUTES,
    ],
  },
];
