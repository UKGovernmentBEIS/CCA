import { DatePipe } from '@angular/common';
import { Component, DoCheck, OnDestroy, OnInit, input, inject } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { BehaviorSubject, combineLatest, filter, takeUntil } from 'rxjs';

import { ErrorMessageComponent } from '../error-message';
import { GovukValidators } from '../error-message';
import { FieldsetDirective, FieldsetHintDirective, LegendDirective } from '../fieldset';
import { FormInput } from '../form/form-input';
import { DateInputValidators } from './date-input.validators';

/*
  eslint-disable
  @angular-eslint/component-selector
 */
@Component({
  selector: 'div[govuk-date-input]',
  templateUrl: './date-input.component.html',
  providers: [DatePipe],
  imports: [ErrorMessageComponent, ReactiveFormsModule, FieldsetHintDirective, LegendDirective, FieldsetDirective],
})
export class DateInputComponent extends FormInput implements ControlValueAccessor, OnInit, DoCheck, OnDestroy {
  private readonly datePipe = inject(DatePipe);
  private readonly fb = inject(FormBuilder);

  readonly label = input<string>();
  readonly hint = input<string>();
  readonly text = input<string>();
  readonly min = input<Date>();
  readonly max = input<Date>();
  readonly isRequired = input<boolean>();

  formGroup = this.fb.group(
    {
      day: this.fb.control<number>(null),
      month: this.fb.control<number>(null),
      year: this.fb.control<number>(null),
    },
    { updateOn: 'change' },
  );

  private readonly dateValidator: ValidatorFn = () => this.validate();
  private validatedControl: AbstractControl;
  private touch$ = new BehaviorSubject(false);
  private min$ = new BehaviorSubject(null);
  private max$ = new BehaviorSubject(null);
  private onChange: (value: Partial<{ year: number; month: number; day: number }>) => void;
  private onBlur: () => any;

  constructor() {
    super();
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.validatedControl = this.control;
    this.validatedControl.addValidators(this.dateValidator);
    this.control.updateValueAndValidity();

    this.touch$
      .pipe(
        takeUntil(this.destroy$),
        filter((value) => value),
      )
      .subscribe(() => this.formGroup.markAllAsTouched());

    combineLatest([this.min$, this.max$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.control.updateValueAndValidity());

    this.formGroup.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        filter(() => !!this.onChange),
      )
      .subscribe((value) => this.onChange(value));
  }

  override ngDoCheck(): void {
    super.ngDoCheck();

    if (this.touch$.getValue() !== this.control.touched) {
      this.touch$.next(this.control.touched);
    }

    const min = this.min();
    if (this.min$.getValue() !== min) {
      this.min$.next(min);
    }

    const max = this.max();
    if (this.max$.getValue() !== max) {
      this.max$.next(max);
    }
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();

    if (this.validatedControl?.hasValidator(this.dateValidator)) {
      this.validatedControl.removeValidators(this.dateValidator);
      this.validatedControl.updateValueAndValidity({ emitEvent: false });
    }
  }

  hasFieldError(identifier: string): boolean {
    if (this.shouldDisplayErrors) {
      const fieldControl = this.formGroup.get(identifier);

      return (
        fieldControl.invalid ||
        (!fieldControl.value && this.control.errors?.incomplete) ||
        this.control.errors?.minDate ||
        this.control.errors?.maxDate ||
        this.control.errors?.invalidDate ||
        (identifier === 'day' && (this.control.errors?.leapYear || this.control.errors?.day))
      );
    } else {
      return false;
    }
  }

  registerOnChange(onChange: (_: Date) => void): void {
    this.onChange = (values) =>
      onChange(
        this.formGroup.invalid || DateInputValidators.getCombinedValidationResults(this.formGroup, false)
          ? null
          : DateInputValidators.buildDate(values),
      );
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: Date | null): void {
    if (value) {
      this.formGroup.setValue({
        day: value.getUTCDate(),
        month: value.getUTCMonth() + 1,
        year: value.getUTCFullYear(),
      });
    } else {
      this.formGroup.reset();
    }
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.formGroup.disable();
    } else {
      this.formGroup.enable();
    }
  }

  onInputBlur(): void {
    if (Object.values(this.formGroup.controls).every((control) => control.touched)) {
      this.onBlur();
    }
  }

  private validate(): ValidationErrors {
    return {
      ...this.formGroup.get('day').errors,
      ...this.formGroup.get('month').errors,
      ...this.formGroup.get('year').errors,
      ...this.combinedRulesValidator(),
      ...this.beforeOrAfterDateValidator(),
    };
  }

  // basic validators
  private combinedRulesValidator(): ValidationErrors {
    const validationResults = DateInputValidators.getCombinedValidationResults(this.formGroup, this.isRequired());

    const errorMessage = validationResults?.isEmpty
      ? 'Enter a date'
      : validationResults?.isIncomplete
        ? 'Enter a full date'
        : validationResults?.isUnrealDate
          ? 'Enter a real date'
          : '';
    return {
      ...GovukValidators.builder(
        errorMessage,
        DateInputValidators.combinedRulesValidator(this.isRequired()),
      )(this.formGroup),
    };
  }

  // other validators
  private beforeOrAfterDateValidator(): ValidationErrors {
    const errors = DateInputValidators.minMaxDateValidator(this.min(), this.max())(this.control);

    return errors?.minDate
      ? { minDate: `This date must be the same as or after ${this.datePipe.transform(this.min(), 'd MMMM y', 'UTC')}` }
      : errors?.maxDate
        ? {
            maxDate: `This date must be the same as or before ${this.datePipe.transform(this.max(), 'd MMMM y', 'UTC')}`,
          }
        : null;
  }
}
