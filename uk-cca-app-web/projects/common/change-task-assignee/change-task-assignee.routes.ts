import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { AssignmentSuccessComponent } from './assignment-confirmation';
import { ChangeAssigneeComponent } from './change-assignee';

export const ROUTES: Routes = [
  {
    path: '',
    title: 'Reassign task',
    component: ChangeAssigneeComponent,
    data: { backlink: '../', breadcrumb: 'Reassign task' },
  },
  {
    path: 'success',
    title: 'Task reassigned',
    component: AssignmentSuccessComponent,
    canDeactivate: [() => inject(RequestTaskStore).setTaskReassignedTo(null)],
  },
];
