import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AssignmentSuccessComponent } from '@common/change-task-assignee/components/assignment-confirmation';
import { ChangeAssigneeComponent } from '@common/change-task-assignee/components/change-assignee';
import { RequestTaskStore } from '@common/request-task/+state';

export const ROUTES: Routes = [
  {
    path: '',
    component: ChangeAssigneeComponent,
    data: { backlink: '../', breadcrumb: 'Reassign task' },
  },
  {
    path: 'success',
    component: AssignmentSuccessComponent,
    canDeactivate: [() => inject(RequestTaskStore).setTaskReassignedTo(null)],
  },
];
