import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { FormIdentity, GenericRequestTaskPayload } from '../types';

export type SubtaskOperation = 'SAVE_SUBTASK' | 'SUBMIT_SUBTASK' | string;

/**
 * Simple handler class for applying side effects to a task's state based on changes in a specific subtask/step.
 */
export abstract class SideEffect implements FormIdentity {
  abstract subtask: string | undefined;
  abstract step: string | undefined;
  on: SubtaskOperation[] = ['SAVE_SUBTASK'];

  protected store = inject(RequestTaskStore);

  /**
   * Handle task-wide side effects triggered by the saving of a subtask
   */
  abstract apply(payload: GenericRequestTaskPayload, subtask?: string): Observable<GenericRequestTaskPayload>;
}
