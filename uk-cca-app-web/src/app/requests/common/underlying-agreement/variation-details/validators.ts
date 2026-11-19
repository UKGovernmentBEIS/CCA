import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { VariationDetailsFormModel } from './form.provider';

export function changesRequiredFieldsValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors => {
    const group = control as FormGroup<VariationDetailsFormModel>;
    if (!group || !(group instanceof FormGroup)) return null;

    const requireOperatorAssentValue = group.controls.requireOperatorAssent.value;
    const dontRequireOperatorAssentValue = group.controls.dontRequireOperatorAssent.value;
    const otherChangesValue = group.controls.otherChanges.value;

    return !requireOperatorAssentValue?.length && !dontRequireOperatorAssentValue?.length && !otherChangesValue?.length
      ? { requiredChanges: 'Select at least 1 checkbox to proceed' }
      : null;
  };
}
