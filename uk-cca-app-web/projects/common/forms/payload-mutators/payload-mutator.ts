import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { FormIdentity, GenericRequestTaskPayload } from '../types';

/**
 * Simple handler class for applying direct mutation to a task's state based on changes in a specific subtask/step.
 */
export abstract class PayloadMutator implements FormIdentity {
  abstract subtask: string;

  protected store = inject(RequestTaskStore);

  /**
   * Applies payload mutation based on current subtask/step being saved
   * @param payload The task's payload
   * @param step The current wizard step
   * @param userInput The direct input of the user through the form (wizard)
   */
  abstract apply(
    payload: GenericRequestTaskPayload,
    step: string,
    userInput: any,
  ): Observable<GenericRequestTaskPayload>;
}
