import { FormControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Checks if filesize is 0
 */
export function emptyFileValidator(message: string): ValidatorFn {
  return (control: FormControl): ValidationErrors | null => {
    const file = control.value;

    if (file && file.size === 0) {
      return { fileEmpty: { message } };
    }

    return null;
  };
}
