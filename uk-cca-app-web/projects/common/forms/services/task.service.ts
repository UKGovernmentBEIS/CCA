import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { concatMap, map, Observable, tap } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { WIZARD_FLOW_MANAGERS, WizardFlowManager } from '../form-flow';
import { PayloadMutatorsHandler } from '../payload-mutators';
import { SideEffectsHandler } from '../side-effects';
import { GenericRequestTaskPayload } from '../types';
import { TaskApiService } from './task-api.service';

export abstract class TaskService {
  protected store = inject(RequestTaskStore);
  protected apiService = inject(TaskApiService);
  protected payloadMutators = inject(PayloadMutatorsHandler);
  protected sideEffects = inject(SideEffectsHandler);
  protected wizardFlowManagers: WizardFlowManager[] = inject(WIZARD_FLOW_MANAGERS);

  abstract get payload(): GenericRequestTaskPayload;
  abstract set payload(payload: GenericRequestTaskPayload);

  saveSubtask(subtask: string, step: string, route: ActivatedRoute, userInput: any): Observable<string> {
    return this.payloadMutators.mutate(subtask, step, this.payload, userInput).pipe(
      concatMap((payload) => this.sideEffects.apply(subtask, step, payload, 'SAVE_SUBTASK')),
      concatMap((payload) => this.apiService.save(payload)),
      tap((payload) => (this.payload = payload)),
      concatMap(() => this.flowManagerForSubtask(subtask).nextStep(step, route)),
    );
  }

  submitSubtask(subtask: string): Observable<boolean> {
    return this.sideEffects.apply(subtask, null, this.payload, 'SUBMIT_SUBTASK').pipe(
      concatMap((payload) => this.apiService.save(payload)),
      tap((payload) => (this.payload = payload)),
      map(() => true),
    );
  }

  submit(): Observable<void> {
    return this.apiService.submit();
  }

  private flowManagerForSubtask(subtask: string): WizardFlowManager {
    const flowManager = this.wizardFlowManagers.find((sfm) => sfm.subtask === subtask) ?? null;
    if (!flowManager) {
      console.error(`###TaskService### :: Could not find WizardFlowManager for subtask: ${subtask}`);
    }
    return flowManager;
  }
}
