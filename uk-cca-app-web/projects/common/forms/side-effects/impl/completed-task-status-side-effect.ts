import { Inject } from '@angular/core';

import { Observable, of } from 'rxjs';

import { produce } from 'immer';

import { GenericRequestTaskPayload } from '../../types';
import { SideEffect } from '../side-effect';
import { SUBTASK_TO_PROP_MAP, TASK_SECTIONS_COMPLETED_PROP } from './subtask-to-prop-map.provider';

export class CompletedTaskStatusSideEffect extends SideEffect {
  override subtask = undefined;
  override step = undefined;
  override on = ['SUBMIT_SUBTASK'];

  constructor(
    @Inject(SUBTASK_TO_PROP_MAP) private readonly subtaskToPropMap: Record<string, string>,
    @Inject(TASK_SECTIONS_COMPLETED_PROP) private readonly sectionsCompletedProp: string,
  ) {
    super();
  }

  apply(payload: GenericRequestTaskPayload, subtask: string): Observable<GenericRequestTaskPayload> {
    if (!(subtask in this.subtaskToPropMap)) {
      throw new Error(
        `CompletedTaskStatusSideEffect :: You must provide a "SUBTASK_TO_PROP_MAP" containing a value for key "${subtask}"`,
      );
    }
    return of(
      produce(payload, (p) => {
        // TODO :: May need to set this dynamically
        p[this.sectionsCompletedProp][this.subtaskToPropMap[subtask]] = 'COMPLETED';
      }),
    );
  }
}
