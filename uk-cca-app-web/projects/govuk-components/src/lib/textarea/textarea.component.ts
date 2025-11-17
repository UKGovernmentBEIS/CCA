import { AfterViewInit, Component, input, inject, computed } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl, ReactiveFormsModule } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import { ErrorMessageComponent } from '../error-message';
import { FormService } from '../form';
import { FormInput } from '../form/form-input';
import { LabelSizeType } from './label-size.type';

/*
  eslint-disable
  @typescript-eslint/no-empty-function,
  @angular-eslint/component-selector
*/
@Component({
  selector: 'div[govuk-textarea]',
  templateUrl: './textarea.component.html',
  imports: [ReactiveFormsModule, ErrorMessageComponent],
})
export class TextareaComponent extends FormInput implements ControlValueAccessor, AfterViewInit {
  private static readonly WARNING_PERCENTAGE = 0.99;

  readonly label = input<string>();
  readonly labelSize = input<LabelSizeType>();
  readonly hint = input<string>();
  readonly rows = input('5');
  readonly maxLength = input<number>();

  onBlur: (_: any) => any;

  readonly currentLabelSize = computed(() => {
    switch (this.labelSize()) {
      case 'small':
        return 'govuk-label govuk-label--s';
      case 'medium':
        return 'govuk-label govuk-label--m';
      case 'large':
        return 'govuk-label govuk-label--l';
      default:
        return 'govuk-label';
    }
  });

  constructor() {
    const ngControl = inject(NgControl, { self: true, optional: true });
    const formService = inject(FormService);
    const container = inject(ControlContainer, { optional: true });

    super(ngControl, formService, container);
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(onBlur: any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(): void {}

  getInputValue(event: Event): string {
    return (event.target as HTMLTextAreaElement).value;
  }

  handleBlur(value: string): void {
    this.onBlur(value);
  }

  exceedsMaxLength(length: number): boolean {
    return length > this.maxLength();
  }

  approachesMaxLength(length: number): boolean {
    return !this.exceedsMaxLength(length) && length >= this.maxLength() * TextareaComponent.WARNING_PERCENTAGE;
  }

  ngAfterViewInit(): void {
    this.control.valueChanges
      .pipe(
        distinctUntilChanged((prev, curr) => prev === curr),
        tap((value) => {
          const trimmedValue = value ? (value.trim() === '' ? null : value.trim()) : value;
          this.control.setValue(trimmedValue, {
            emitEvent: false,
            emitViewToModelChange: false,
            emitModelToViewChange: false,
          });
        }),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }
}
