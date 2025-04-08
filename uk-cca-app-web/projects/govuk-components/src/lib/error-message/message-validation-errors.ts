import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export type MessageValidationErrors = ValidationErrors & Record<string, string>;

export declare interface MessageValidatorFn extends ValidatorFn {
  (control: AbstractControl): MessageValidationErrors | null;
}
