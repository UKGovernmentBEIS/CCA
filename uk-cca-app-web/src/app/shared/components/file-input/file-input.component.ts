import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  effect,
  ElementRef,
  HostBinding,
  inject,
  Injector,
  input,
  OnInit,
  Signal,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ControlValueAccessor, FormGroupDirective, NgControl, NgForm, UntypedFormControl } from '@angular/forms';

import { map, startWith } from 'rxjs';

import { ErrorMessageComponent, FormService, MessageValidationErrors } from '@netz/govuk-components';

import { FileUploadListComponent } from '../file-upload-list/file-upload-list.component';
import { LabelSizeType } from '../text-input/label-size.type';
import { FileUploadService } from './file-upload.service';
import { FileUpload, FileUploadEvent } from './file-upload-event';

@Component({
  selector: 'cca-file-input',
  templateUrl: './file-input.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FileUploadListComponent, ErrorMessageComponent],
})
export class FileInputComponent implements OnInit, ControlValueAccessor {
  private readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly root = inject(FormGroupDirective, { optional: true });
  private readonly rootNgForm = inject(NgForm, { optional: true });
  private readonly formService = inject(FormService);
  private readonly fileUploadService = inject(FileUploadService);
  private readonly injector = inject(Injector);
  private readonly destroyRef = inject(DestroyRef);

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass() {
    return this.shouldDisplayErrors();
  }

  protected readonly listTitle = input<string>();
  protected readonly label = input<string>('Upload a file');
  protected readonly text = input<string>();
  protected readonly showFilesizeHint = input<boolean>(true);
  protected readonly hint = input<string>();
  protected readonly accepted = input<string>('*/*');
  protected readonly downloadUrl = input<(uuid: string) => string | string[]>();
  protected readonly labelSize = input<LabelSizeType>();

  protected readonly fileInput = viewChild<ElementRef<HTMLInputElement>>('input');
  protected readonly filePickerButton = viewChild<ElementRef<HTMLButtonElement>>('filePickerButton');

  protected readonly isDisabled = signal(false);
  protected readonly shouldDisplayErrors = signal(false);
  private readonly value = signal<FileUpload>(null);

  /**
   * The control value is always a `FileUpload`, but the stored file may be identified by its uuid
   * alone, without a file reference. Show that entry too: what the field displays is what its caller
   * reads back when the form is saved, so nothing may be hidden from the user while still being saved.
   */
  private readonly fileValue = computed<FileUpload>(() => {
    const file = this.value();

    return file?.file || file?.uuid ? file : null;
  });

  private readonly uploadProgress = toSignal(this.fileUploadService.uploadProgress$);
  private controlErrors: Signal<MessageValidationErrors | null>;

  private onChange: (value: FileUpload) => void;
  private onBlur: () => void;

  constructor() {
    this.ngControl.valueAccessor = this;

    effect(() => {
      const progress = this.uploadProgress();
      const value = this.fileValue();

      if (!progress?.uuid || progress.file !== value?.file || value?.uuid === progress.uuid) return;

      this.onChange?.({ ...value, uuid: progress.uuid, dimensions: value.dimensions });
    });
  }

  protected readonly currentLabelSize = computed(() => {
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

  protected readonly describedBy = computed(() =>
    [
      this.showFilesizeHint() ? this.id + '-hint-file-size' : null,
      this.hint() ? this.id + '-hint' : null,
      this.shouldDisplayErrors() ? this.id + '-error' : null,
    ]
      .filter(Boolean)
      .join(' '),
  );

  protected readonly uploadedFiles = computed<FileUploadEvent[]>(() => {
    const value = this.fileValue();
    const errors = this.controlErrors();
    const progress = this.uploadProgress();

    const fileEvent =
      value && progress?.file && progress.file === value.file
        ? ({ file: value.file, ...progress } as FileUploadEvent)
        : value
          ? ({ ...value, progress: null, ...(errors ? { errors } : {}) } as FileUploadEvent)
          : null;

    return fileEvent
      ? [{ ...fileEvent, ...(fileEvent.uuid && { downloadUrl: this.downloadUrl()(fileEvent.uuid) }) }]
      : [];
  });

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.root ?? this.rootNgForm;
  }

  ngOnInit(): void {
    this.controlErrors = toSignal(
      this.control.statusChanges.pipe(
        map(() => this.control.errors),
        startWith(this.control.errors),
      ),
      { initialValue: null, injector: this.injector },
    );

    this.refreshErrorState();
    this.control.statusChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.refreshErrorState());
    this.form?.ngSubmit?.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.refreshErrorState());
  }

  registerOnChange(onChange: (value: FileUpload) => void): void {
    this.onChange = (value) => {
      this.value.set(value);
      onChange(value);
    };
  }

  registerOnTouched(onBlur: () => void): void {
    this.onBlur = onBlur;
  }

  writeValue(value: FileUpload): void {
    this.value.set(value);
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled.set(isDisabled);
  }

  onFilePickerButtonClick(): void {
    this.fileInput().nativeElement.click();
  }

  onFileBlur(): void {
    this.onBlur();
  }

  onFileChange(event: Event): void {
    const files = (event.target as HTMLInputElement).files;

    if (files.length === 1) {
      const [file] = files;

      // Show the picked file, and start its upload, straight away. The dimensions are only used by
      // the image validators, and probing them can throw or never report back for a bitmap the
      // browser cannot decode, which used to leave the field empty and the file unuploaded.
      this.uploadFile(file);
      this.addImageDimensions(file);
    }

    this.fileInput().nativeElement.value = null;
    this.filePickerButton().nativeElement.focus();
  }

  onFileDeleteClick(): void {
    this.onChange(null);
    this.fileInput().nativeElement.value = null;
    this.filePickerButton().nativeElement.focus();
  }

  private refreshErrorState(): void {
    this.shouldDisplayErrors.set(this.control?.invalid && (!this.form || this.form.submitted));
  }

  private uploadFile(file: File): void {
    this.onChange({ file, uuid: null, dimensions: null });
  }

  /** Resolves the dimensions of an image and applies them once known, which may be never. */
  private addImageDimensions(file: File): void {
    if (!this.isImage(file)) return;

    let fileAsDataURL: string;

    try {
      fileAsDataURL = window.URL.createObjectURL(file);
    } catch {
      return;
    }

    this.getImageFileDimensionsResolver(fileAsDataURL)
      .then((dimensions) => this.setImageDimensions(file, dimensions))
      .catch(() => {
        // The dimensions are optional: the picked file is already displayed and uploading.
      })
      .finally(() => window.URL.revokeObjectURL?.(fileAsDataURL));
  }

  /** Applies probed dimensions only while the field still holds the file they belong to. */
  private setImageDimensions(file: File, dimensions: { width: number; height: number }): void {
    const value = this.value();

    if (value?.file === file) this.onChange?.({ ...value, dimensions });
  }

  private isImage(file: File) {
    return file.type?.startsWith('image/');
  }

  private getImageFileDimensionsResolver = (dataURL: string) =>
    new Promise<{ width: number; height: number }>((resolve, reject) => {
      const img = new Image();

      img.onload = () => {
        resolve({
          width: img.width,
          height: img.height,
        });
      };

      img.onerror = function () {
        reject();
      };

      img.src = dataURL;
    });
}
