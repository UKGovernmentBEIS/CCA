import { AsyncPipe } from '@angular/common';
import { Component, ElementRef, inject, input, OnInit, viewChild } from '@angular/core';
import { ControlValueAccessor, FormGroupDirective, NgControl, NgForm, UntypedFormControl } from '@angular/forms';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  map,
  Observable,
  scan,
  startWith,
  Subject,
  tap,
  withLatestFrom,
} from 'rxjs';

import { ErrorMessageComponent, FormService, MessageValidationErrors } from '@netz/govuk-components';

import { FileUploadService } from '../file-input/file-upload.service';
import { FileUpload, FileUploadEvent } from '../file-input/file-upload-event';
import { FileUploadListComponent } from '../file-upload-list/file-upload-list.component';

@Component({
  selector: 'cca-multiple-file-input[baseDownloadUrl]',
  templateUrl: './multiple-file-input.component.html',
  standalone: true,
  imports: [FileUploadListComponent, AsyncPipe, ErrorMessageComponent],
})
export class MultipleFileInputComponent implements ControlValueAccessor, OnInit {
  private readonly ngControl = inject(NgControl, { self: true, optional: true });
  private readonly formService = inject(FormService);
  private readonly fileUploadService = inject(FileUploadService);
  private readonly root = inject(FormGroupDirective, { optional: true });
  private readonly rootNgForm = inject(NgForm, { optional: true });

  protected readonly headerSize = input<'m' | 's'>('m');
  protected readonly listTitle = input<string>();
  protected readonly label = input<string>('Upload a file');
  protected readonly hint = input<string>();
  protected readonly accepted = input<string>('*/*');
  protected readonly uploadStatusText = input<string>('Uploading files, please wait');
  protected readonly dropzoneHintText = input<string>('Drag and drop files here or');
  protected readonly dropzoneButtonText = input<string>('Choose files');
  protected readonly baseDownloadUrl = input<string>();

  protected readonly fileInput = viewChild<ElementRef<HTMLInputElement>>('input');

  protected readonly uploadStatusText$ = new Subject<string>();
  protected uploadedFiles$: Observable<FileUploadEvent[]>;
  protected isFocused = false;
  protected isDraggedOver = false;
  protected isDisabled: boolean;

  private onChange: (value: FileUpload[]) => any;
  private onBlur: () => any;
  private value$ = new BehaviorSubject<FileUpload[]>([]);

  constructor() {
    this.ngControl.valueAccessor = this;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.form.submitted);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.root ?? this.rootNgForm;
  }

  ngOnInit(): void {
    this.uploadedFiles$ = combineLatest([
      this.value$,
      this.control.statusChanges.pipe(
        startWith(this.control.status),
        //as we now have async validators we should update errors on final statuses (VALID, INVALID)
        filter((status) => status !== 'PENDING'),
        map(() => this.control.errors),
        startWith(this.control.errors),
      ),
      this.fileUploadService.uploadProgress$.pipe(
        withLatestFrom(this.value$),
        filter(([uploadEvent, value]) => value?.some(({ file }) => file === uploadEvent.file)),
        tap(([uploadEvent, value]) => {
          if (uploadEvent.uuid) {
            value.splice(
              value.findIndex((upload) => upload.file === uploadEvent.file),
              1,
              uploadEvent,
            );
          }
        }),
        map(([uploadEvent]) => uploadEvent),
        startWith(undefined),
      ),
    ]).pipe(
      scan(
        (acc, [existing, errors, uploadEvent]) =>
          (existing ?? []).map((existingFile, index) => {
            return {
              ...existingFile,
              ...acc.find(({ file, uuid }) => (uuid && uuid === existingFile.uuid) || file === existingFile.file),
              ...(uploadEvent?.file === existingFile.file ? uploadEvent : {}),
              ...(existingFile.uuid && { downloadUrl: this.baseDownloadUrl() + `${existingFile.uuid}` }),
              errors: this.applyRowErrors(errors, index),
            };
          }),
        [],
      ),
    );
  }

  registerOnChange(onChange: (value: FileUpload[]) => any): void {
    this.onChange = (value) => {
      this.value$.next(value);
      onChange(value);
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: FileUploadEvent[]): void {
    this.value$.next(value ?? []);
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  onFileChange(event: Event): void {
    const files = (event?.target as HTMLInputElement)?.files;
    this.uploadFiles(files);
    this.fileInput().nativeElement.value = null;
    this.fileInput().nativeElement.focus();
  }

  onFileFocus(): void {
    this.isFocused = true;
  }

  onFileBlur(): void {
    this.isFocused = false;
    this.onBlur();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();

    if (!this.isDisabled) {
      this.isDraggedOver = true;
    }
  }

  onDragLeave(): void {
    this.isDraggedOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDraggedOver = false;

    if (!this.isDisabled) {
      this.uploadStatusText$.next(this.uploadStatusText());
      this.uploadFiles(event.dataTransfer.files);
    }
  }

  onFileDeleteClick(deletedIndex: number): void {
    this.onChange(this.value$.getValue().filter((_, index) => index !== deletedIndex));
  }

  private uploadFiles(files: FileList): void {
    this.onChange(this.value$.getValue().concat(Array.from(files).map((file) => ({ file }))));
  }

  private applyRowErrors(errors: MessageValidationErrors, index: number): MessageValidationErrors {
    const rowErrors = Object.entries(errors ?? {}).filter(([key]) => key.endsWith(`-${index}`));

    return rowErrors.length === 0
      ? null
      : rowErrors.reduce((acc, [key, value]) => ({ ...(acc ? acc : {}), [key]: value }) as any, null);
  }
}
