import { NgClass } from '@angular/common';
import { Component, input, inject, computed, model } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl, ReactiveFormsModule } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message';
import { FormService } from '../form';
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
    const ngControl = inject(NgControl, { self: true, optional: true });
    const formService = inject(FormService);
    const container = inject(ControlContainer, { optional: true });

    super(ngControl, formService, container);
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(): void {}
}
