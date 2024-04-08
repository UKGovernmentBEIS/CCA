import { inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { concatMap, map, Observable } from 'rxjs';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';

import { RequestTaskStore } from '../../request-task/+state';

/**
 * A manager class for handling differentiated wizard form flows inside a subtask
 *
 * @example
 * // @Injectable
 * class SampleSubtaskWizardFlowManager extends StepFlowManager {
 *   subtask = 'sample-subtask';
 *
 *   nextStepPath(currentStep: string): Observable<string> {
 *     switch (currentStep) {
 *       case 'first-step':
 *         return of('second-step');
 *       case 'second-step':
 *         return this.resolveAfterSecondStepPath();
 *       default:
 *         return of('first-step');
 *     }
 *   }
 *
 *   private resolveAfterSecondStepPath(): Observable<string> {
 *     return this.store.select(selectSecondStepAnswer).pipe(
 *       take(1),
 *       map(answer => answer === 'yes' ? 'third-step' : 'fourth-step'),
 *     );
 *   }
 * }
 */
export abstract class WizardFlowManager {
  abstract subtask: string;
  protected store = inject(RequestTaskStore);
  protected router = inject(Router);

  nextStep(currentStep: string, route: ActivatedRoute): Observable<string> {
    return this.nextStepPath(currentStep).pipe(
      concatMap((path) => {
        return fromPromise(this.router.navigate([path], { relativeTo: route })).pipe(map(() => path));
      }),
    );
  }

  abstract nextStepPath(currentStep: string): Observable<string>;
}
