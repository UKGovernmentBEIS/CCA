import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  HostBinding,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NgControl,
  NgForm,
  ReactiveFormsModule,
  TouchedChangeEvent,
  UntypedFormControl,
  UntypedFormGroup,
} from '@angular/forms';

import { filter } from 'rxjs';

import { ErrorMessageComponent, FieldsetDirective, FormService } from '@netz/govuk-components';
import { transformPhoneInput } from '@shared/pipes';
import { CountryCallingCodeService, CountryService, UK_COUNTRY_CODES } from '@shared/services';
import { UKCountryCodes } from '@shared/types';

import { PhoneNumberDTO } from 'cca-api';

type CountryOption = { text: string; value: string; code: string };

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'div[cca-phone-input]',
  templateUrl: './phone-input.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, ErrorMessageComponent, FieldsetDirective],
})
export class PhoneInputComponent implements OnInit, ControlValueAccessor {
  private readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly formService = inject(FormService);
  private readonly destroy$ = inject(DestroyRef);
  private readonly container = inject(ControlContainer, { optional: true });
  private readonly countryCallingCodeService = inject(CountryCallingCodeService);

  private readonly countries = inject(CountryService).countries;

  protected readonly label = input<string>(undefined);
  protected readonly hint = input<string>(undefined);

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  protected readonly disabled = signal(false);

  valueTransform = transformPhoneInput;

  formGroup = new UntypedFormGroup({
    countryCode: new UntypedFormControl(),
    number: new UntypedFormControl(),
  });

  protected readonly phoneCodes = computed<CountryOption[]>(() => {
    const emptyOption: CountryOption = { text: '--', value: '', code: '' };
    const ukCountries: CountryOption[] = [];
    const otherCountries: CountryOption[] = [];

    this.countries().forEach((country) => {
      const callingCode = this.countryCallingCodeService.getCountryCallingCode(country.code);

      const option = {
        text: `${UKCountryCodes.GB === country.code ? UKCountryCodes.UK : country.code} (${callingCode})`,
        value: String(callingCode),
        code: country.code,
      };

      if ([...UK_COUNTRY_CODES, 'GB'].includes(country.code)) {
        ukCountries.push(option);
      } else {
        otherCountries.push(option);
      }
    });

    return [...this.sortByProp(ukCountries, 'text'), emptyOption, ...this.sortByProp(otherCountries, 'text')];
  });

  private readonly submitted = signal(false);
  private readonly controlInvalid = signal(false);

  protected readonly shouldDisplayErrors = computed(() => this.controlInvalid() && (!this.form || this.submitted()));

  onChange: (phone: PhoneNumberDTO) => void;
  onBlur: () => void;

  constructor() {
    const ngControl = this.ngControl;

    ngControl.valueAccessor = this;
  }

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass() {
    return this.shouldDisplayErrors();
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
    this.controlInvalid.set(this.control?.invalid ?? false);

    this.formGroup.valueChanges
      .pipe(
        takeUntilDestroyed(this.destroy$),
        filter(() => !!this.onChange),
      )
      .subscribe((value: { countryCode?: string; number?: string }) =>
        this.onChange({ countryCode: value.countryCode || null, number: value.number || null }),
      );

    // Bridge form-control state into signals so the OnPush template (and host bindings)
    // update without manual change detection.
    this.control?.statusChanges
      .pipe(takeUntilDestroyed(this.destroy$))
      .subscribe(() => this.controlInvalid.set(this.control.invalid));

    this.form?.ngSubmit.pipe(takeUntilDestroyed(this.destroy$)).subscribe(() => this.submitted.set(true));

    // Propagate the host control's touched state to the inner group (replaces ngDoCheck polling).
    this.control?.events
      .pipe(
        takeUntilDestroyed(this.destroy$),
        filter((event) => event instanceof TouchedChangeEvent && event.touched),
      )
      .subscribe(() => this.formGroup.markAllAsTouched());
  }

  onInputBlur(): void {
    if (Object.values(this.formGroup.controls).every((control) => control.touched)) {
      this.onBlur();
    }
  }

  registerOnChange(fn: (phone: PhoneNumberDTO) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(onBlur: () => void): void {
    this.onBlur = onBlur;
  }

  writeValue(value: PhoneNumberDTO): void {
    if (value) {
      this.formGroup.get('countryCode').setValue(value.countryCode);
      this.formGroup.get('number').setValue(value.number);
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  sortByProp(items: CountryOption[], prop: keyof CountryOption) {
    return items.sort((a, b) => (a[prop] > b[prop] ? 1 : -1));
  }
}
