import { NgTemplateOutlet } from '@angular/common';
import { AfterContentChecked, AfterContentInit, Component, input, contentChildren, inject } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message';
import { FieldsetDirective, FieldsetHintDirective, LegendDirective, LegendSizeType } from '../fieldset';
import { FormService } from '../form';
import { FormInput } from '../form/form-input';
import { RadioOptionComponent } from './radio-option/radio-option.component';

/*
  eslint-disable
  @angular-eslint/component-selector
 */
@Component({
  selector: 'div[govuk-radio]',
  standalone: true,
  templateUrl: './radio.component.html',
  imports: [LegendDirective, FieldsetDirective, FieldsetHintDirective, ErrorMessageComponent, NgTemplateOutlet],
})
export class RadioComponent<T>
  extends FormInput
  implements AfterContentInit, AfterContentChecked, ControlValueAccessor
{
  readonly legend = input<string>();
  readonly hint = input<string>();
  readonly radioSize = input<'medium' | 'large'>('large');
  readonly isInline = input(false);
  readonly legendSize = input<LegendSizeType>('normal');

  readonly options = contentChildren(RadioOptionComponent);

  private onChange: (_: T) => any;
  private onBlur: () => any;
  private isDisabled: boolean;

  constructor() {
    const ngControl = inject(NgControl, { self: true, optional: true });
    const formService = inject(FormService);
    const container = inject(ControlContainer, { optional: true });

    super(ngControl, formService, container);
  }

  ngAfterContentInit() {
    this.setDisabledState(this.isDisabled);
    this.writeValue(this.control.value);
  }

  ngAfterContentChecked(): void {
    this.options().forEach((option, index) => {
      option.index = index;
      option.groupIdentifier = this.identifier;
      option.registerOnChange(this.onChange);
    });
    this.registerOnTouched(this.onBlur);
  }

  writeValue(value: T): void {
    this.options()?.forEach((option) => option.writeValue(value));
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = (option) => {
      this.writeValue(option);
      onChange(option);
    };
    this.options()?.forEach((option) => option.registerOnChange(this.onChange));
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
    this.options()?.forEach((option) => option.registerOnTouched(this.onBlur));
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.options()?.forEach((option) => option.setDisabledState(isDisabled));
  }
}
