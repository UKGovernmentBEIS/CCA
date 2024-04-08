import { Routes } from '@angular/router';

import { RequestTaskPageComponent } from '@common/request-task/components/request-task-page';

export const REQUEST_TASK_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        component: RequestTaskPageComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () => import('../change-task-assignee').then((r) => r.ROUTES),
      },
      {
        path: 'cancel',
        loadChildren: () => import('../cancel-task').then((r) => r.ROUTES),
      },
    ],
  },
];
