import { Routes } from '@angular/router';

import { WORKFLOW_NOTES_ROUTES } from '../../../../workflow-history-tab/workflow-details/notes-tab/notes.routes';
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
          ...WORKFLOW_NOTES_ROUTES,
        ],
      },
    ],
  },
];
