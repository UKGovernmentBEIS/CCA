import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { UnderlyingAgreementVariationActivationTaskService } from './services/underlying-agreement-variation-activation-task.service';
import { UnderlyingAgreementVariationActivationTaskApiService } from './services/underlying-agreement-variation-activation-task-api.service';
import { ProvideEvidencePayloadMutator } from './subtasks/provide-evidence/provide-evidence-payload.mutator';
import { ProvideEvidenceSideEffect } from './subtasks/provide-evidence/provide-evidence-side-effect';
import { ProvideEvidenceStepFlowManager } from './subtasks/provide-evidence/provide-evidence-step-flow-manager';

export function provideUnderlyingAgreementVariationActivationPayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ProvideEvidencePayloadMutator },
  ]);
}

export function provideUnderlyingAgreementVariationActivationTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: UnderlyingAgreementVariationActivationTaskApiService },
    { provide: TaskService, useClass: UnderlyingAgreementVariationActivationTaskService },
  ]);
}

export function provideUnderlyingAgreementVariationActivationSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([{ provide: SIDE_EFFECTS, multi: true, useClass: ProvideEvidenceSideEffect }]);
}

export function provideUnderlyingAgreementVariationActivationStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ProvideEvidenceStepFlowManager },
  ]);
}
