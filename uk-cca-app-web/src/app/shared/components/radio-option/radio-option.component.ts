import { Component, HostBinding, Input, Optional, Self } from '@angular/core';
import { ControlValueAccessor, NgControl, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';

import { FormService } from '@netz/govuk-components';

/* eslint-disable
   @typescript-eslint/no-empty-function,
*/
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'div[cca-radio-option]',
  templateUrl: './radio-option.component.html',
  standalone: true,
  imports: [ReactiveFormsModule],
})
export class RadioOptionComponent implements ControlValueAccessor {
  @Input() index: string;
  @Input() value: string;
  @Input() label: string;
  @Input() isDisabled: boolean;

  @HostBinding('class.govuk-radios__item') readonly govukRadiosItem = true;

  constructor(
    @Self() @Optional() readonly ngControl: NgControl,
    private readonly formService: FormService,
  ) {
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
