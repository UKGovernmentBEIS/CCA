import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function notEqualToCurrentValidator(current: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value != null && +control.value === current)
      return { sameAsCurrent: `The new amount cannot be equal to the current amount` };

    return null;
  };
}
