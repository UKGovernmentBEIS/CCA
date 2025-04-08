import { InjectionToken, type Provider } from '@angular/core';
import { FormBuilder, type FormControl, type FormGroup } from '@angular/forms';

export const WORKFLOW_HISTORY_TAB_FORM_PROVIDER = new InjectionToken('Workflow history tab form provider');

export type WorkflowHistoryTabFormModel = FormGroup<{
  requestTypes: FormControl<string[]>;
  requestStatuses: FormControl<string[]>;
}>;

export const WorkflowHistoryTabFormProvider: Provider = {
  provide: WORKFLOW_HISTORY_TAB_FORM_PROVIDER,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    return fb.group({
      requestTypes: fb.control<string[]>([]),
      requestStatuses: fb.control<string[]>([]),
    });
  },
};
