import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { AssigneeUserInfoDTO, TasksAssignmentService } from 'cca-api';

export function createCandidateAssigneesResolver(taskType: string): ResolveFn<AssigneeUserInfoDTO[]> {
  return (): Observable<AssigneeUserInfoDTO[]> => {
    const tasksAssignmentService = inject(TasksAssignmentService);
    const requestTaskStore = inject(RequestTaskStore);
    const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    return tasksAssignmentService.getCandidateAssigneesByTaskType(requestTaskId, taskType);
  };
}
