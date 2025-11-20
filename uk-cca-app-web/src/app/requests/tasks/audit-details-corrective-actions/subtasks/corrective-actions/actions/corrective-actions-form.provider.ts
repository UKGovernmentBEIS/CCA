import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { CorrectiveAction } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';

export type CorrectiveActionFormModel = FormGroup<{
  title: FormControl<CorrectiveAction['title']>;
  details: FormControl<CorrectiveAction['details']>;
  deadline: FormControl<Date>;
}>;

export type CorrectiveActionsFormModel = FormGroup<{
  correctiveActions: FormArray<CorrectiveActionFormModel>;
}>;

export const CORRECTIVE_ACTIONS_FORM = new InjectionToken<CorrectiveActionsFormModel>('Corrective actions form');

export const correctiveActionsFormProvider: Provider = {
  provide: CORRECTIVE_ACTIONS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const correctiveActions = store.select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
      ?.correctiveActions?.actions;

    const productControls = correctiveActions?.length
      ? correctiveActions?.map((action) => createCorrectiveActionFormGroup(fb, action))
      : [createCorrectiveActionFormGroup(fb)];

    return fb.group({
      correctiveActions: fb.array<CorrectiveActionFormModel>(productControls, {
        validators: [GovukValidators.required('Add at least one corrective action')],
        updateOn: 'change',
      }),
    });
  },
};

export function createCorrectiveActionFormGroup(
  fb: FormBuilder,
  correctiveAction?: CorrectiveAction,
): CorrectiveActionFormModel {
  return fb.group({
    title: fb.control<CorrectiveAction['title']>(correctiveAction?.title ?? null),
    details: fb.control<CorrectiveAction['details']>(correctiveAction?.details ?? null, [
      GovukValidators.required('Enter details of the action'),
    ]),
    deadline: fb.control<Date>(correctiveAction?.deadline ? new Date(correctiveAction?.deadline) : null, [
      GovukValidators.required('Enter a deadline date'),
    ]),
  });
}
