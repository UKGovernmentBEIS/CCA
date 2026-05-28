import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { futureDateValidator } from '@shared/validators';

import { NonComplianceConclusionDetails } from 'cca-api';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';

export type ProvideDetailsFormModel = FormGroup<{
  complianceRestored: FormControl<NonComplianceConclusionDetails['complianceRestored'] | null>;
  complianceRestoredDate: FormControl<Date | string | null>;
  penaltyPaid: FormControl<NonComplianceConclusionDetails['penaltyPaid'] | null>;
  penaltyPaymentDate: FormControl<Date | string | null>;
  comment: FormControl<NonComplianceConclusionDetails['comment']>;
  penaltyOutcome: FormControl<NonComplianceConclusionDetails['penaltyOutcome'] | null>;
}>;

export const PROVIDE_DETAILS_FORM = new InjectionToken<ProvideDetailsFormModel>('Provide conclusion details form');

export const ProvideDetailsFormProvider: Provider = {
  provide: PROVIDE_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const details = store.select(nonComplianceConclusionQuery.selectConclusionDetails)();

    const group = fb.group({
      complianceRestored: fb.control<boolean | null>(details?.complianceRestored ?? null, [
        GovukValidators.required('Select yes if compliance has been restored'),
      ]),
      complianceRestoredDate: fb.control<Date | string | null>(
        details?.complianceRestoredDate ? new Date(details.complianceRestoredDate) : null,
      ),
      penaltyPaid: fb.control<boolean | null>(details?.penaltyPaid ?? null, [
        GovukValidators.required('Select yes if the operator has paid the penalty'),
      ]),
      penaltyPaymentDate: fb.control<Date | string | null>(
        details?.penaltyPaymentDate ? new Date(details.penaltyPaymentDate) : null,
      ),
      comment: fb.control<string>(details?.comment ?? null, [
        GovukValidators.required('Enter your comments on the status of compliance'),
        GovukValidators.maxLength(10000, 'Comments must be 10000 characters or less'),
      ]),
      penaltyOutcome: fb.control<NonComplianceConclusionDetails['penaltyOutcome'] | null>(
        details?.penaltyOutcome ?? null,
        [GovukValidators.required('Select an option')],
      ),
    });

    updateDateValidation(group.controls.complianceRestored.value, group.controls.complianceRestoredDate);
    updateDateValidation(group.controls.penaltyPaid.value, group.controls.penaltyPaymentDate);

    group.controls.complianceRestored.valueChanges.pipe(takeUntilDestroyed()).subscribe((value) => {
      updateDateValidation(value, group.controls.complianceRestoredDate);
    });

    group.controls.penaltyPaid.valueChanges.pipe(takeUntilDestroyed()).subscribe((value) => {
      updateDateValidation(value, group.controls.penaltyPaymentDate);
    });

    return group;
  },
};

function updateDateValidation(parentValue: boolean | null, dateControl: FormControl<Date | string | null>): void {
  if (parentValue === true) {
    dateControl.setValidators([
      GovukValidators.required('Enter a date'),
      futureDateValidator('This date cannot be in the future'),
    ]);
  } else {
    dateControl.clearValidators();
    if (parentValue === false) {
      dateControl.reset(null);
    }
  }
  dateControl.updateValueAndValidity();
}
