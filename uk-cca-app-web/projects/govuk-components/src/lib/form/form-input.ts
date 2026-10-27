import { ChangeDetectorRef, Directive, HostBinding, inject, OnDestroy, OnInit } from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NgControl,
  NgForm,
  UntypedFormControl,
} from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { describedBy as joinDescribedBy } from './described-by';
import { FormService } from './form.service';

@Directive()
export abstract class FormInput implements ControlValueAccessor, OnInit, OnDestroy {
  private readonly ngControl = inject(NgControl);
  private readonly formService = inject(FormService);
  private readonly container = inject(ControlContainer);
  protected readonly cdr = inject(ChangeDetectorRef);

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  protected readonly destroy$ = new Subject<void>();
  private isSubmitted = false;

  protected constructor() {
    this.ngControl.valueAccessor = this;
  }

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass(): boolean {
    return this.shouldDisplayErrors;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.isSubmitted);
  }

  // Ids of the elements that describe this control (hint, error, character count), joined so the
  // control only references descriptions that are actually rendered.
  protected describedBy(...ids: (string | null | undefined | false)[]): string | null {
    return joinDescribedBy(...ids);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    this.form?.ngSubmit.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.isSubmitted = true;
      this.cdr?.markForCheck();
    });

    // With OnPush, control state changes (value, validity, touched) can originate outside this
    // component's template — async validators resolving, or programmatic setValue/setErrors/
    // markAllAsTouched. Re-marking for check keeps the error styling in sync.
    this.control?.events.pipe(takeUntil(this.destroy$)).subscribe(() => this.cdr?.markForCheck());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  abstract writeValue(value: any): void;

  abstract registerOnChange(onChange: (value: any) => any): void;

  abstract registerOnTouched(onBlur: () => any): void;
}
