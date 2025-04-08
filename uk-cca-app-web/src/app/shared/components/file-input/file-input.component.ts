import { AsyncPipe } from '@angular/common';
import { Component, ElementRef, HostBinding, inject, Input, input, OnInit, viewChild } from '@angular/core';
import { ControlValueAccessor, FormGroupDirective, NgControl, NgForm, UntypedFormControl } from '@angular/forms';

import { BehaviorSubject, combineLatest, filter, map, merge, Observable, startWith, tap, withLatestFrom } from 'rxjs';

import { ErrorMessageComponent, FormService } from '@netz/govuk-components';

import { FileUploadListComponent } from '../file-upload-list/file-upload-list.component';
import { LabelSizeType } from '../text-input/label-size.type';
import { FileUploadService } from './file-upload.service';
import { FileUpload, FileUploadEvent } from './file-upload-event';

@Component({
  selector: 'cca-file-input',
  templateUrl: './file-input.component.html',
  standalone: true,
  imports: [FileUploadListComponent, ErrorMessageComponent, AsyncPipe],
})
export class FileInputComponent implements OnInit, ControlValueAccessor {
  private readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly root = inject(FormGroupDirective, { optional: true });
  private readonly rootNgForm = inject(NgForm, { optional: true });
  private readonly formService = inject(FormService);
  private readonly fileUploadService = inject(FileUploadService);

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass() {
    return this.shouldDisplayErrors;
  }

  listTitle = input<string>();
  label = input<string>('Upload a file');
  text = input<string>();
  showFilesizeHint = input<boolean>(true);
  hint = input<string>();
  accepted = input<string>('*/*');
  downloadUrl = input<(uuid: string) => string | string[]>();
  currentLabelSize = 'govuk-label';
  fileInput = viewChild<ElementRef<HTMLInputElement>>('input');

  uploadedFiles$: Observable<FileUploadEvent[]>;
  isDisabled: boolean;
  onFileBlur: () => any;

  private value$ = new BehaviorSubject<FileUpload>(null);
  private onChange: (value: FileUpload) => any;

  constructor() {
    this.ngControl.valueAccessor = this;
  }

  @Input() set labelSize(size: LabelSizeType) {
    switch (size) {
      case 'small':
        this.currentLabelSize = 'govuk-label govuk-label--s';
        break;
      case 'medium':
        this.currentLabelSize = 'govuk-label govuk-label--m';
        break;
      case 'large':
        this.currentLabelSize = 'govuk-label govuk-label--l';
        break;
      default:
        this.currentLabelSize = 'govuk-label';
        break;
    }
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.form.submitted);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.root ?? this.rootNgForm;
  }

  ngOnInit(): void {
    this.uploadedFiles$ = merge(
      combineLatest([
        this.value$.pipe(map((value) => (value ? { ...value, progress: null } : null))),
        this.control.statusChanges.pipe(
          map(() => this.control.errors),
          startWith(this.control.errors),
        ),
      ]).pipe(map(([value, errors]) => (value ? { ...value, errors } : null))),
      this.fileUploadService.uploadProgress$.pipe(
        withLatestFrom(this.value$),
        filter(([fileEvent, value]) => fileEvent.file === value?.file),
        tap(([uploadEvent, value]) => {
          if (uploadEvent.uuid) {
            this.onChange({ ...value, uuid: uploadEvent.uuid, dimensions: value.dimensions });
          }
        }),
        map(([fileEvent]) => fileEvent),
      ),
    ).pipe(
      map((fileEvent) =>
        fileEvent
          ? [
              {
                ...fileEvent,
                ...(fileEvent.uuid && { downloadUrl: this.downloadUrl()(fileEvent.uuid) }),
              },
            ]
          : [],
      ),
    );
  }

  registerOnChange(onChange: (value: FileUpload) => any): void {
    this.onChange = (value) => {
      this.value$.next(value);
      onChange(value);
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onFileBlur = onBlur;
  }

  writeValue(value: FileUploadEvent): void {
    this.value$.next(value);
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  onFileChange(event: Event): void {
    const files = (event.target as HTMLInputElement).files;

    if (files.length === 1) {
      if (this.isImage(files[0])) {
        const fileAsDataURL = window.URL.createObjectURL(files[0]);
        this.getImageFileDimensionsResolver(fileAsDataURL)
          .then((dimensions) => {
            this.uploadFile(files[0], dimensions);
          })
          .catch(() => {
            this.uploadFile(files[0], null);
          });
      } else {
        this.uploadFile(files[0], null);
      }
    }
  }

  onFileDeleteClick(): void {
    this.onChange(null);
    this.fileInput().nativeElement.value = null;
  }

  private uploadFile(file: File, dimensions): void {
    this.onChange({ file, uuid: null, dimensions });
  }

  private isImage(file: File) {
    return file['type'].split('/')[0] == 'image';
  }

  private getImageFileDimensionsResolver = (dataURL) =>
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
