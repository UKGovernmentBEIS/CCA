import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { WithdrawAdminTerminationTaskService } from './services/withdraw-admin-termination-task.service';
import { WithdrawAdminTerminationTaskApiService } from './services/withdraw-admin-termination-task-api.service';
import { ReasonForWithdrawAdminTerminationStepFlowManager } from './subtasks/reason-for-withdraw-admin-termination/reason-for-withdraw-admin-termination-step-flow-manager';
import { ReasonForWithdrawAdminTerminationSubmitSideEffect } from './subtasks/reason-for-withdraw-admin-termination/reason-for-withdraw-admin-termination-submit-side-effect';
import { WithdrawAdminTerminationSubmitPayloadMutator } from './withdraw-admin-termination-payload.mutator';

export function provideWithdrawAdminTerminationTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: WithdrawAdminTerminationTaskApiService },
    { provide: TaskService, useClass: WithdrawAdminTerminationTaskService },
  ]);
}

export function provideWithdrawAdminTerminationPayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: WithdrawAdminTerminationSubmitPayloadMutator },
  ]);
}

export function provideWithdrawAdminTerminationStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ReasonForWithdrawAdminTerminationStepFlowManager },
  ]);
}

export function provideWithdrawAdminTerminationSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: SIDE_EFFECTS, multi: true, useClass: ReasonForWithdrawAdminTerminationSubmitSideEffect },
  ]);
}
