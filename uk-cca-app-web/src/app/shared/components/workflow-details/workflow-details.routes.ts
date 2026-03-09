import { Routes } from '@angular/router';

import { NOTES_ROUTES } from '../notes-tab/notes.routes';
import { WorkflowDetailsResolver } from './workflow-details.resolver';

export const WORKFLOW_DETAILS_ROUTES: Routes = [
  {
    path: ':workflowId',
    resolve: { workflowDetailsItemsAndActions: WorkflowDetailsResolver },
    data: {
      breadcrumb: ({ workflowDetailsItemsAndActions }) => `${workflowDetailsItemsAndActions.workflowDetails.id}`,
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
      ...NOTES_ROUTES,
    ],
  },
];
