import { NgClass } from '@angular/common';
import { Component, input, computed, model } from '@angular/core';
import { ControlValueAccessor, ReactiveFormsModule } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message';
import { FormInput } from '../form/form-input';
import { GovukSelectOption } from './select.interface';
import { GovukTextWidthClass } from './select.type';

/*
  eslint-disable
  @typescript-eslint/no-empty-function,
  @angular-eslint/component-selector
*/
@Component({
  selector: 'div[govuk-select]',
  templateUrl: './select.component.html',
  imports: [ReactiveFormsModule, NgClass, ErrorMessageComponent],
})
export class SelectComponent extends FormInput implements ControlValueAccessor {
  readonly options = model<GovukSelectOption[]>();
  readonly widthClass = input<GovukTextWidthClass>();
  readonly label = input<string>();

  readonly isLabelHidden = computed(() => (this.label() ? false : true));
  readonly currentLabel = computed(() => this.label() ?? 'Select');

  constructor() {
    super();
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(): void {}
}
