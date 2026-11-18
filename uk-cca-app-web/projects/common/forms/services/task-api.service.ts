import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { TasksService } from 'cca-api';

import { GenericRequestTaskPayload } from '../types';

export abstract class TaskApiService {
  protected readonly store = inject(RequestTaskStore);
  protected readonly service = inject(TasksService);

  /**
   * Performs the api save operation and returns the saved payload
   *
   * @param payload
   * @return The saved payload as observable
   */
  abstract save(payload: GenericRequestTaskPayload): Observable<GenericRequestTaskPayload>;

  abstract submit(): Observable<void>;
}
