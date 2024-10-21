import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { UnderlyingAgreementActivationTaskService } from './services/underlying-agreement-activation-task.service';
import { UnderlyingAgreementActivationTaskApiService } from './services/underlying-agreement-activation-task-api.service';
import { ProvideEvidencePayloadMutator } from './subtasks/provide-evidence/provide-evidence-payload.mutator';
import { ProvideEvidenceSideEffect } from './subtasks/provide-evidence/provide-evidence-side-effect';
import { ProvideEvidenceStepFlowManager } from './subtasks/provide-evidence/provide-evidence-step-flow-manager';

export function provideUNAActivationPayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ProvideEvidencePayloadMutator },
  ]);
}

export function provideUNAActivationTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: UnderlyingAgreementActivationTaskApiService },
    { provide: TaskService, useClass: UnderlyingAgreementActivationTaskService },
  ]);
}

export function provideUNAActivationSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([{ provide: SIDE_EFFECTS, multi: true, useClass: ProvideEvidenceSideEffect }]);
}

export function provideUNAActivationStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ProvideEvidenceStepFlowManager },
  ]);
}
