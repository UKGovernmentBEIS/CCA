import { Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/components';

import { refreshDaysRemainingGuard } from './track-corrective-actions.guard';

export const TRACK_CORRECTIVE_ACTIONS_ROUTES: Routes = [
  {
    path: '',
    canDeactivate: [refreshDaysRemainingGuard],
    children: [
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then((r) => r.TRACK_CORRECTIVE_ACTIONS_COMPLETE_TASK_ROUTES),
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: ':actionId',
        loadChildren: () => import('./subtasks/track-actions/track-actions.routes').then((r) => r.TRACK_ACTIONS_ROUTES),
      },
    ],
  },
];
