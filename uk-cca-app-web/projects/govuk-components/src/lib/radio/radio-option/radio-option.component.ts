import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  TemplateRef,
  input,
  contentChild,
  viewChild,
  inject,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { ConditionalContentDirective } from '../../directives';

@Component({
  selector: 'govuk-radio-option',
  imports: [],
  templateUrl: './radio-option.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RadioOptionComponent<T> implements ControlValueAccessor {
  readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly value = input<T>();
  readonly label = input<string>();
  readonly hint = input<string>();
  readonly divider = input<boolean>();

  readonly conditional = contentChild(ConditionalContentDirective);

  readonly conditionalTemplate = viewChild<TemplateRef<any>>('conditionalTemplate');
  readonly optionTemplate = viewChild<TemplateRef<any>>('optionTemplate');

  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  onChange: (_: T) => any;
  onBlur: () => any;
  groupIdentifier: string;

  get identifier(): string {
    return `${this.groupIdentifier}-option${this.index}`;
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(newValue: T): void {
    this.isChecked = newValue === this.value();
    this.setConditionalDisabledState();
    this.changeDetectorRef.detectChanges();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.setConditionalDisabledState();
    this.changeDetectorRef.markForCheck();
  }

  private setConditionalDisabledState() {
    if (this.isChecked && !this.isDisabled) {
      this.conditional()?.enableControls();
    } else {
      this.conditional()?.disableControls();
    }
  }
}
