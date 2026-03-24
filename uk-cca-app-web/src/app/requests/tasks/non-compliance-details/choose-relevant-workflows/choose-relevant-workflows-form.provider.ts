import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';

export type WorkflowFormControl = FormControl<string | null>;

export type ChooseRelevantWorkflowsFormModel = FormGroup<{
  workflows: FormArray<WorkflowFormControl>;
}>;

export const CHOOSE_RELEVANT_WORKFLOWS_FORM = new InjectionToken<ChooseRelevantWorkflowsFormModel>(
  'Choose relevant workflows form',
);

export const ChooseRelevantWorkflowsFormProvider: Provider = {
  provide: CHOOSE_RELEVANT_WORKFLOWS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const relevantWorkflows = store.select(nonComplianceDetailsQuery.selectRelevantWorkflows)() ?? [];
    const workflowControls = relevantWorkflows.map((workflowId) => createWorkflowFormControl(fb, workflowId));

    return fb.group({
      workflows: fb.array<WorkflowFormControl>(workflowControls),
    });
  },
};

export function createWorkflowFormControl(fb: FormBuilder, value?: string): WorkflowFormControl {
  return fb.control(value ?? null);
}
