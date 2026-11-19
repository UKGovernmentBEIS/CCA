import { MessageValidationErrors } from './message-validation-errors';

/** Prefer a date's specific input error without changing the control's validation state. */
export function displayValidationErrors(errors: MessageValidationErrors | null | undefined) {
  if (!errors?.required) return errors;

  if (errors.isIncomplete || errors.isUnrealDate) {
    const displayedErrors = { ...errors };
    delete displayedErrors.required;
    return displayedErrors;
  }

  if (errors.isEmpty) {
    const displayedErrors = { ...errors };
    delete displayedErrors.isEmpty;
    return displayedErrors;
  }

  return errors;
}
