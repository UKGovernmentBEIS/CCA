import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestTaskPayload } from 'cca-api';

import { RequestTaskStore } from '../../request-task/+state';
import { FormIdentity } from '../form-identity';

/**
 * Simple handler class for applying direct mutation to a task's state based on changes in a specific subtask/step.
 */
export abstract class PayloadMutator implements FormIdentity {
  abstract subtask: string;
  abstract step: string | undefined;

  protected store = inject(RequestTaskStore);

  /**
   * Applies payload mutation based on current subtask/step being saved
   * @param payload The task's payload
   * @param userInput The direct input of the user through the form (wizard)
   */
  abstract apply<T extends RequestTaskPayload>(payload: T, userInput: any): Observable<T>;
}
