import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validates file size to provided fileSize in MB
 */
export function maxFileSizeValidator(maxSizeInMB: number, message: string): ValidatorFn {
  const maxSizeInBytes = maxSizeInMB * 1024 * 1024;

  return (control: AbstractControl): ValidationErrors | null => {
    const file = control.value;

    if (file && file.size > maxSizeInBytes) {
      return { fileTooLarge: { message } };
    }

    return null;
  };
}
