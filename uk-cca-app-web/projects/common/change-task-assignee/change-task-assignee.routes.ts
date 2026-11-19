import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { AssignmentSuccessComponent } from './assignment-confirmation';
import { ChangeAssigneeComponent } from './change-assignee';

export const ROUTES: Routes = [
  {
    title: 'Reassign task',
    path: '',
    component: ChangeAssigneeComponent,
    data: { backlink: '../', breadcrumb: 'Reassign task' },
  },
  {
    title: 'Task reassigned',
    path: 'success',
    component: AssignmentSuccessComponent,
    canDeactivate: [() => inject(RequestTaskStore).setTaskReassignedTo(null)],
  },
];
