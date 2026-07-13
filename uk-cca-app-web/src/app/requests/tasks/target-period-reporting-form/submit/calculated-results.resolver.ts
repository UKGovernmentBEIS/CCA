import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

export const calculatedResultsResolver: ResolveFn<unknown> = () => {
  const store = inject(RequestTaskStore);
  const tasksApiService = inject(TasksApiService);

  const requestTaskId = store.select(requestTaskQuery.selectRequestTaskId)();

  return tasksApiService.saveRequestTaskAction({
    requestTaskId,
    requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CALCULATE_RESULTS',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    },
  });
};
