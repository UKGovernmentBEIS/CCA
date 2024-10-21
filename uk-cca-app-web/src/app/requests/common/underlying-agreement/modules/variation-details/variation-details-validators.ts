import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { VariationDetailsFormModel } from './variation-details-form.provider';

export function changesRequiredFieldsValidator(): ValidatorFn {
  return (group: FormGroup<VariationDetailsFormModel>): ValidationErrors => {
    const facilityChangesValue = group.get('facilityChanges').value;
    const baselineChangesValue = group.get('baselineChanges').value;
    const targetCurrencyChangesValue = group.get('targetCurrencyChanges').value;
    const otherChangesValue = group.get('otherChanges').value;

    return !facilityChangesValue?.length &&
      !baselineChangesValue?.length &&
      !targetCurrencyChangesValue?.length &&
      !otherChangesValue?.length
      ? { requiredChanges: 'Select at least 1 checkbox to proceed' }
      : null;
  };
}
