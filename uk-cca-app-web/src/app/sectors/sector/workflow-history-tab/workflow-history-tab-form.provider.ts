import { InjectionToken, type Provider } from '@angular/core';
import { FormBuilder, type FormControl, type FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

export type WorkflowHistoryTabFormModel = FormGroup<{
  requestTypes: FormControl<string[]>;
  requestStatuses: FormControl<string[]>;
}>;

export const WORKFLOW_HISTORY_TAB_FORM_PROVIDER = new InjectionToken<WorkflowHistoryTabFormModel>(
  'Workflow history tab form provider',
);

export const WorkflowHistoryTabFormProvider: Provider = {
  provide: WORKFLOW_HISTORY_TAB_FORM_PROVIDER,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const queryParamMap = activatedRoute.snapshot.queryParamMap;

    return fb.group({
      requestTypes: fb.control<string[]>(queryParamMap.getAll('requestTypes') || []),
      requestStatuses: fb.control<string[]>(queryParamMap.getAll('requestStatuses') || []),
    });
  },
};
