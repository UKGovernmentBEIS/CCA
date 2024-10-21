import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { UnderlyingAgreementApplicationTaskService } from './services/underlying-agreement-application-task.service';
import { UnderlyingAgreementApplicationTaskApiService } from './services/underlying-agreement-application-task-api.service';
import { AuthorisationAdditionalEvidencePayloadMutator } from './subtasks/authorisation-additional-evidence/authorisation-additional-evidence-payload.mutator';
import { AuthorisationAdditionalEvidenceStepFlowManager } from './subtasks/authorisation-additional-evidence/authorisation-additional-evidence-step-flow-manager';
import { AuthorisationAdditionalEvidenceSubmitSideEffect } from './subtasks/authorisation-additional-evidence/authorisation-additional-evidence-submit-side-effect';
import { Tp5AddBaselineDataSaveSideEffect } from './subtasks/baseline-and-targets/tp5/tp5-add-baseline-data-save-side-effect';
import { Tp5BaselineExistsSaveSideEffect } from './subtasks/baseline-and-targets/tp5/tp5-baseline-exists-save-side-effect';
import { Tp5PayloadMutator } from './subtasks/baseline-and-targets/tp5/tp5-payload.mutator';
import { Tp5StepFlowManager } from './subtasks/baseline-and-targets/tp5/tp5-step-flow-manager';
import { Tp5SubmitSideEffect } from './subtasks/baseline-and-targets/tp5/tp5-submit-side-effect';
import { Tp5TargetCompositionSaveSideEffect } from './subtasks/baseline-and-targets/tp5/tp5-target-composition-save-side-effect';
import { Tp6AddBaselineDataSaveSideEffect } from './subtasks/baseline-and-targets/tp6/tp6-add-baseline-data-save-side-effect';
import { Tp6PayloadMutator } from './subtasks/baseline-and-targets/tp6/tp6-payload.mutator';
import { Tp6StepFlowManager } from './subtasks/baseline-and-targets/tp6/tp6-step-flow-manager';
import { Tp6SubmitSideEffect } from './subtasks/baseline-and-targets/tp6/tp6-submit-side-effect';
import { Tp6TargetCompositionSaveSideEffect } from './subtasks/baseline-and-targets/tp6/tp6-target-composition-save-side-effect';
import { FacilityPayloadMutator } from './subtasks/facility/facility-payload.mutator';
import { FacilityStepFlowManager } from './subtasks/facility/facility-step-flow-manager';
import { FacilitySubmitSideEffects } from './subtasks/facility/facility-submit-side-effects';
import { ManageFacilitiesPayloadMutator } from './subtasks/manage-facilities/manage-facilities-payload.mutator';
import { ManageFacilitiesStepFlowManager } from './subtasks/manage-facilities/manage-facilities-step-flow-manager';
import { ManageFacilitiesSubmitSideEffects } from './subtasks/manage-facilities/manage-facilities-submit-side-effects';
import { ReviewTargetUnitDetailsPayloadMutator } from './subtasks/review-target-unit-details/review-target-unit-details-payload.mutator';
import { ReviewTargetUnitDetailsStepFlowManager } from './subtasks/review-target-unit-details/review-target-unit-details-step-flow-manager';
import { ReviewTargetUnitDetailsSubmitSideEffect } from './subtasks/review-target-unit-details/review-target-unit-details-submit-side-effect';

export function providePayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ReviewTargetUnitDetailsPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ManageFacilitiesPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: FacilityPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: AuthorisationAdditionalEvidencePayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: Tp5PayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: Tp6PayloadMutator },
  ]);
}

export function provideTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: UnderlyingAgreementApplicationTaskApiService },
    { provide: TaskService, useClass: UnderlyingAgreementApplicationTaskService },
  ]);
}

export function provideSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: SIDE_EFFECTS, multi: true, useClass: ReviewTargetUnitDetailsSubmitSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: ManageFacilitiesSubmitSideEffects },
    { provide: SIDE_EFFECTS, multi: true, useClass: FacilitySubmitSideEffects },
    { provide: SIDE_EFFECTS, multi: true, useClass: AuthorisationAdditionalEvidenceSubmitSideEffect },

    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5BaselineExistsSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5TargetCompositionSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5AddBaselineDataSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5SubmitSideEffect },

    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6TargetCompositionSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6AddBaselineDataSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6SubmitSideEffect },
  ]);
}

export function provideStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ReviewTargetUnitDetailsStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ManageFacilitiesStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: FacilityStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: AuthorisationAdditionalEvidenceStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: Tp5StepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: Tp6StepFlowManager },
  ]);
}
