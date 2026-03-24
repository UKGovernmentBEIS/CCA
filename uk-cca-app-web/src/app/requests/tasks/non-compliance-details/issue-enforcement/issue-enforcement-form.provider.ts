import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';

export type IssueEnforcementFormModel = FormGroup<{
  isEnforcementResponseNoticeRequired: FormControl<boolean | null>;
  explanation: FormControl<string | null>;
}>;

export const ISSUE_ENFORCEMENT_FORM = new InjectionToken<IssueEnforcementFormModel>('Issue enforcement form');

export const IssueEnforcementFormProvider: Provider = {
  provide: ISSUE_ENFORCEMENT_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const nonComplianceDetails = store.select(nonComplianceDetailsQuery.selectNonComplianceDetails)();

    const group = fb.group({
      isEnforcementResponseNoticeRequired: fb.control(
        nonComplianceDetails?.isEnforcementResponseNoticeRequired ?? null,
        [GovukValidators.required('Make a selection')],
      ),
      explanation: fb.control(nonComplianceDetails?.explanation ?? null),
    });

    updateExplanationValidation(group.controls.isEnforcementResponseNoticeRequired.value, group.controls.explanation);

    group.controls.isEnforcementResponseNoticeRequired.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((isEnforcementResponseNoticeRequired) => {
        updateExplanationValidation(isEnforcementResponseNoticeRequired, group.controls.explanation);
      });

    return group;
  },
};

function updateExplanationValidation(
  isEnforcementResponseNoticeRequired: boolean | null,
  explanationControl: FormControl<string | null>,
): void {
  if (isEnforcementResponseNoticeRequired === false) {
    explanationControl.setValidators([
      GovukValidators.required('Enter an explanation for not issuing an Enforcement Response Notice'),
    ]);
  } else {
    explanationControl.clearValidators();
  }

  explanationControl.updateValueAndValidity({ emitEvent: false });
}
