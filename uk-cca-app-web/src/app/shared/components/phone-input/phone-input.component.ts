import { AsyncPipe } from '@angular/common';
import { Component, DestroyRef, DoCheck, HostBinding, Input, OnInit, Optional, Self } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NgControl,
  NgForm,
  ReactiveFormsModule,
  UntypedFormControl,
  UntypedFormGroup,
} from '@angular/forms';

import { BehaviorSubject, filter, map, Observable, of } from 'rxjs';

import { ErrorMessageComponent, FieldsetDirective, FormService, GovukSelectOption } from '@netz/govuk-components';
import { transformPhoneInput } from '@shared/pipes';
import { COUNTRIES, CountryCallingCodeService, UK_COUNTRY_CODES } from '@shared/services';
import { UKCountryCodes } from '@shared/types';

import { PhoneNumberDTO } from 'cca-api';

type CountryOption = { text: string; value: string };

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'div[cca-phone-input]',
  templateUrl: './phone-input.component.html',
  imports: [ReactiveFormsModule, ErrorMessageComponent, AsyncPipe, FieldsetDirective],
})
export class PhoneInputComponent implements OnInit, DoCheck, ControlValueAccessor {
  @Input() label: string;
  @Input() hint?: string;
  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;
  disabled: boolean;
  valueTransform = transformPhoneInput;

  formGroup = new UntypedFormGroup({
    countryCode: new UntypedFormControl(),
    number: new UntypedFormControl(),
  });

  phoneCodes$: Observable<GovukSelectOption<string>[]> = of(COUNTRIES).pipe(
    map((countries) => {
      const emptyOption: CountryOption[] = [{ text: '--', value: '' }];
      const ukCountries: CountryOption[] = [];
      const otherCountries: CountryOption[] = [];

      countries.forEach((country) => {
        const callingCode = this.countryCallingCodeService.getCountryCallingCode(country.code);

        const option = {
          text: `${UKCountryCodes.GB === country.code ? UKCountryCodes.UK : country.code} (${callingCode})`,
          value: String(callingCode),
        };

        if ([...UK_COUNTRY_CODES, 'GB'].includes(country.code)) {
          ukCountries.push(option);
        } else {
          otherCountries.push(option);
        }
      });

      return [...this.sortByProp(ukCountries, 'text'), ...emptyOption, ...this.sortByProp(otherCountries, 'text')];
    }),
  );

  onChange: (phone: PhoneNumberDTO) => void;
  onBlur: () => void;
  private touch$ = new BehaviorSubject(false);

  constructor(
    @Self() @Optional() private readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly destroy$: DestroyRef,
    @Optional() private readonly container: ControlContainer,
    private readonly countryCallingCodeService: CountryCallingCodeService,
  ) {
    ngControl.valueAccessor = this;
  }

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass() {
    return this.shouldDisplayErrors;
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.form.submitted);
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    this.formGroup.valueChanges
      .pipe(
        takeUntilDestroyed(this.destroy$),
        filter(() => !!this.onChange),
      )
      .subscribe((value) => this.onChange({ countryCode: value.countryCode || null, number: value.number || null }));
  }

  ngDoCheck(): void {
    if (this.touch$.getValue() !== this.control.touched && this.control.touched) {
      this.formGroup.markAllAsTouched();
      this.touch$.next(this.control.touched);
    }
  }

  onInputBlur(): void {
    if (Object.values(this.formGroup.controls).every((control) => control.touched)) {
      this.onBlur();
      this.touch$.next(true);
    }
  }

  registerOnChange(fn: (phone: PhoneNumberDTO) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: PhoneNumberDTO): void {
    if (value) {
      this.formGroup.get('countryCode').setValue(value.countryCode);
      this.formGroup.get('number').setValue(value.number);
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  sortByProp(items: CountryOption[], prop: keyof CountryOption) {
    return items.sort((a, b) => (a[prop] > b[prop] ? 1 : -1));
  }
}
