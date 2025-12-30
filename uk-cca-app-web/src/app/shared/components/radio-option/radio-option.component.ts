import { Component, HostBinding, inject, input } from '@angular/core';
import { ControlValueAccessor, NgControl, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';

import { FormService } from '@netz/govuk-components';

/* eslint-disable
   @typescript-eslint/no-empty-function,
*/
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'div[cca-radio-option]',
  templateUrl: './radio-option.component.html',
  imports: [ReactiveFormsModule],
})
export class RadioOptionComponent implements ControlValueAccessor {
  protected readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly formService = inject(FormService);

  protected readonly index = input<string>(undefined);
  protected readonly value = input<string>(undefined);
  protected readonly label = input<string>(undefined);
  protected readonly isDisabled = input<boolean>(undefined);

  @HostBinding('class.govuk-radios__item') readonly govukRadiosItem = true;

  constructor() {
    const ngControl = this.ngControl;

    ngControl.valueAccessor = this;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  writeValue = (_: any): void => {};

  registerOnChange = (_: any): void => {};

  registerOnTouched = (_: any): void => {};
}
