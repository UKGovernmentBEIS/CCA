import { Routes } from '@angular/router';

import { NOTES_ROUTES } from '../notes-tab/notes.routes';
import { ResolvedWorkflowDetails, WorkflowDetailsResolver } from './workflow-details.resolver';

export const WORKFLOW_DETAILS_ROUTES: Routes = [
  {
    path: ':workflowId',
    data: {
      breadcrumb: ({ workflowDetailsItemsAndActions }: { workflowDetailsItemsAndActions: ResolvedWorkflowDetails }) =>
        `${workflowDetailsItemsAndActions.workflowDetails.id}`,
    },
    resolve: { workflowDetailsItemsAndActions: WorkflowDetailsResolver },
    children: [
      {
        title: 'Workflow details',
        path: '',
        loadComponent: () => import('./workflow-details.component').then((c) => c.WorkflowDetailsComponent),
      },
      {
        path: 'timeline',
        loadChildren: () => import('@requests/timeline').then((r) => r.TIMELINE_ROUTES),
      },
      ...NOTES_ROUTES,
    ],
  },
];
