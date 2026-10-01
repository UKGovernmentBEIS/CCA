import { AbstractControl, ValidatorFn } from '@angular/forms';

type DateInput = Partial<{ year: number; month: number; day: number }>;

// @dynamic
export class DateInputValidators {
  static dateFieldValidator(identifier: string, min: number, max: number): ValidatorFn {
    return (control: AbstractControl): Record<string, boolean> | null =>
      control.value && (control.value < min || control.value > max) ? { [identifier]: true } : null;
  }

  static minMaxDateValidator(min: Date, max: Date): ValidatorFn {
    return (control: AbstractControl): Record<string, boolean> | null =>
      control.value && min && control.value < min
        ? { minDate: true }
        : control.value && max && control.value > max
          ? { maxDate: true }
          : null;
  }

  static dateIncompleteValidator: ValidatorFn = (control: AbstractControl) => {
    const day = control.get('day')?.value;
    const month = control.get('month')?.value;
    const year = control.get('year')?.value;
    return (day || month || year) && (!year || !month || !day) ? { incomplete: true } : null;
  };

  static incorrectDayValidator: ValidatorFn = (control: AbstractControl) => {
    const day = control.get('day')?.value;
    const month = control.get('month')?.value;

    return (Number(day) > 29 && Number(month) === 2) ||
      (DateInputValidators.isShortMonth(Number(month)) && Number(day) > 30) ||
      Number(day) > 31
      ? { day: true }
      : null;
  };

  static isLeapYear(year: number): boolean {
    return !(year & 3 || (!(year % 25) && year & 15));
  }

  static isShortMonth(month: number): boolean {
    return month === 2 || month === 4 || month === 6 || month === 9 || month === 11;
  }

  static buildDate({ year, month, day }: DateInput): Date | null {
    return !year || !month || !day ? null : new Date(Date.UTC(Number(year), Number(month) - 1, Number(day)));
  }

  static combinedRulesValidator = (isRequired = false): ValidatorFn => {
    return (control: AbstractControl) => {
      return DateInputValidators.getCombinedValidationResults(control, isRequired);
    };
  };

  static getCombinedValidationResults(control: AbstractControl, isRequired: boolean) {
    return isRequired && DateInputValidators.isEmpty(control)
      ? { isEmpty: true }
      : DateInputValidators.isIncomplete(control)
        ? { isIncomplete: true }
        : DateInputValidators.isUnrealDate(control) && !DateInputValidators.isEmpty(control)
          ? { isUnrealDate: true }
          : null;
  }

  static isEmpty(control: AbstractControl): boolean {
    const day = control.get('day')?.value;
    const month = control.get('month')?.value;
    const year = control.get('year')?.value;
    return !day && !month && !year;
  }

  static isIncomplete(control: AbstractControl): boolean {
    const day = control.get('day')?.value;
    const month = control.get('month')?.value;
    const year = control.get('year')?.value;
    return (day || month || year) && (!year || !month || !day);
  }

  static isUnrealDate(control: AbstractControl): boolean {
    const day = control.get('day')?.value;
    const month = control.get('month')?.value;
    const year = control.get('year')?.value;

    const isBetweenTheAllowedValues = (value: number, min: number, max: number) => {
      return /^\d+$/.test(value?.toString()) && value >= min && value <= max;
    };

    const isNotCorrectLeapYearDate = () => {
      return Number(day) === 29 && Number(month) === 2 && !DateInputValidators.isLeapYear(Number(year));
    };

    const isIncorrectDay =
      (Number(day) > 29 && Number(month) === 2) ||
      (DateInputValidators.isShortMonth(Number(month)) && Number(day) > 30) ||
      Number(day) > 31;

    const isValidYear = Number(year).toString().length === 4;

    return (
      !isBetweenTheAllowedValues(day, 1, 31) ||
      !isBetweenTheAllowedValues(month, 1, 12) ||
      !isBetweenTheAllowedValues(year, 1900, 2100) ||
      isNotCorrectLeapYearDate() ||
      isIncorrectDay ||
      !isValidYear
    );
  }
}
