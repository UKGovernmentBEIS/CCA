import { NgClass } from '@angular/common';
import { booleanAttribute, Component, input, computed, model, ChangeDetectionStrategy } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, NgClass, ErrorMessageComponent],
})
export class SelectComponent extends FormInput implements ControlValueAccessor {
  readonly options = model<GovukSelectOption[]>();
  readonly widthClass = input<GovukTextWidthClass>();
  readonly label = input<string>();
  readonly labelHidden = input(false, { transform: booleanAttribute });
  readonly hint = input<string>();

  readonly isLabelHidden = computed(() => this.labelHidden() || !this.label());
  readonly currentLabel = computed(() => this.label() ?? 'Select');

  constructor() {
    super();
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(): void {}
}
