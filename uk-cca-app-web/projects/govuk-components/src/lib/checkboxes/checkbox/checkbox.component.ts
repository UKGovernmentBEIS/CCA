import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  TemplateRef,
  contentChild,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';
import { ConditionalContentDirective } from '../../directives';

@Component({
  selector: 'govuk-checkbox',
  templateUrl: './checkbox.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckboxComponent<T> implements ControlValueAccessor {
  readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly value = input<T>();
  readonly label = input<string>();
  readonly hint = input<string>();
  readonly divider = input<string | null>(null);

  readonly conditional = contentChild(ConditionalContentDirective);

  readonly conditionalTemplate = viewChild<TemplateRef<any>>('conditionalTemplate');
  readonly optionTemplate = viewChild<TemplateRef<any>>('checkboxTemplate');

  isChecked = signal(false);
  isDisabled = signal(false);
  isTouched = signal(false);
  index: number;
  groupIdentifier: string;

  get identifier(): string {
    return `${this.groupIdentifier}-${this.index}`;
  }

  onChange: (event: Event) => void;
  onBlur: () => void;

  registerOnChange(fn: () => any): void {
    this.onChange = (event: Event) => {
      const checked = (event.target as HTMLInputElement).checked;
      this.writeValue(checked);
      fn();
    };
  }

  registerOnTouched(fn: () => any): void {
    this.onBlur = () => {
      this.isTouched.set(true);
      fn();
    };
  }

  writeValue(value: boolean): void {
    this.isChecked.set(value);
    this.updateConditionalState();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled.set(isDisabled);
    this.updateConditionalState();
  }

  private updateConditionalState() {
    if (this.isChecked() && !this.isDisabled()) {
      this.conditional()?.enableControls();
    } else {
      this.conditional()?.disableControls();
    }
  }
}
