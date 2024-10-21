import { Routes } from '@angular/router';

import { RequestTaskPageComponent } from './components';

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
        loadChildren: () => import('@netz/common/change-task-assignee').then((r) => r.ROUTES),
      },
    ],
  },
];
