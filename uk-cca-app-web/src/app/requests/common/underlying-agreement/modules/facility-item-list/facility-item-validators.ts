import { AbstractControl, ValidatorFn } from '@angular/forms';

import { Facility } from 'cca-api';

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): Record<string, string> | null => {
    const date = new Date();
    return control.value && control.value > date
      ? { invalidDate: 'The exclusion date can not be a future date' }
      : null;
  };
}

export function atLeastOneActiveValidator(): ValidatorFn {
  return (control: AbstractControl): Record<string, string> | null => {
    const facilities = (control.value as Array<Facility>).filter((f) => f.status !== 'EXCLUDED');
    return facilities.length > 0 ? null : { invalid: 'Your agreement must have at least one new or live facility' };
  };
}
