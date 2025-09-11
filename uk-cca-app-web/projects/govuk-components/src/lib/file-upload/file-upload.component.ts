import { Component, HostBinding, input, inject, computed } from '@angular/core';
import { ControlValueAccessor, NgControl, UntypedFormControl } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message';
import { FormService } from '../form';

/*
  eslint-disable
  @typescript-eslint/no-unused-vars,
  @typescript-eslint/no-empty-function
*/
@Component({
  selector: 'div[govukFileUpload],govuk-file-upload',
  standalone: true,
  imports: [ErrorMessageComponent],
  templateUrl: './file-upload.component.html',
})
export class FileUploadComponent implements ControlValueAccessor {
  private readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly formService = inject(FormService);

  readonly accepted = input<string>();
  readonly isMultiple = input<boolean>();
  readonly label = input<string>();

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass(): boolean {
    return this.control?.invalid && this.control?.touched;
  }

  isLabelHidden = computed(() => !this.label());
  currentLabel = computed(() => this.label() ?? 'Upload file');

  constructor() {
    const ngControl = this.ngControl;
    ngControl.valueAccessor = this;
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  onChange(event: Event): void {
    this.control.patchValue((event?.target as HTMLInputElement)?.files);
  }

  onBlur(): void {
    this.control.markAsTouched();
  }

  writeValue(_: any): void {}

  registerOnChange(_: any): void {}

  registerOnTouched(_: any): void {}
}
