import { Routes } from '@angular/router';

export const AUDIT_DETAILS_CORRECTIVE_ACTIONS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'audit-details',
        loadChildren: () => import('./subtasks/audit-details/audit-details.routes').then((r) => r.AUDIT_DETAILS_ROUTES),
      },
      {
        path: 'corrective-actions',
        loadChildren: () =>
          import('./subtasks/corrective-actions/corrective-actions.routes').then((r) => r.CORRECTIVE_ACTIONS_ROUTES),
      },
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then(
            (r) => r.AUDIT_DETAILS_CORRECTIVE_ACTIONS_COMPLETE_TASK_ROUTES,
          ),
      },
    ],
  },
];
