import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn } from '@angular/forms';

import {
  catchError,
  defaultIfEmpty,
  defer,
  filter,
  forkJoin,
  iif,
  map,
  Observable,
  of,
  shareReplay,
  Subject,
  tap,
} from 'rxjs';

import { MessageValidationErrors } from '@netz/govuk-components';

import { FileUuidDTO } from 'cca-api';

import { FileUploadEvent } from './file-upload-event';
import { FileValidators, MAX_FILE_SIZE_BYTES } from './file-validators';

export type FileUploadRequest<T = FileUuidDTO> = (file: File) => Observable<HttpEvent<T>>;

/**
/**
 * File controls are filled both by the payload builders and by the inputs themselves, and their
 * value can be one entry or a list of them. Normalise the value so a validator never has to assume
 * which shape it was given. Falsy entries are kept so that the `-<index>` error keys a validator
 * produces keep pointing at the same entry.
 */
const toFileEvents = (value: unknown): FileUploadEvent[] =>
  Array.isArray(value) ? value : value ? [value as FileUploadEvent] : [];

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  private readonly uploadProgressSubject = new Subject<FileUploadEvent>();
  readonly uploadProgress$ = this.uploadProgressSubject.asObservable();

  upload(request: FileUploadRequest): AsyncValidatorFn {
    const requestUpload = this.requestUpload(request);

    // Cache files that already have been attempted to be uploaded, to avoid replays of errored or in progress files
    const attempts = new WeakMap<File, Observable<MessageValidationErrors>>();

    return ({ value }: AbstractControl) => {
      // The value can be shaped as one entry or as a list of them, so upload the first entry that still
      // has a file to upload instead of reading the value as a single event.
      const event = toFileEvents(value).find((item) => item?.file instanceof File && !item.uuid);

      if (!event || this.isFileEmpty(event) || this.isFileLarge(event)) {
        return of(null);
      }

      const file = event.file as File;

      if (!attempts.has(file)) {
        attempts.set(file, requestUpload(file).pipe(shareReplay({ bufferSize: 1, refCount: false })));
      }

      return attempts.get(file);
    };
  }

  uploadMany(request: FileUploadRequest): AsyncValidatorFn {
    const requestUpload = this.requestUpload(request);

    // Cache files that already have been attempted to be uploaded, to avoid replays of errored or in progress files
    const attempts = new WeakMap<File, Observable<MessageValidationErrors>>();

    return ({ value }: AbstractControl) =>
      forkJoin(
        toFileEvents(value).map((fileEvent) => {
          const file = fileEvent?.file;

          if (!(file instanceof File)) {
            return of(null);
          }

          if (!attempts.has(file)) {
            attempts.set(
              file,
              iif(
                () =>
                  !this.isFileAlreadyUploaded(fileEvent) &&
                  !this.isFileEmpty(fileEvent) &&
                  !this.isFileLarge(fileEvent),
                defer(() => requestUpload(file)),
                of(null),
              ).pipe(shareReplay({ bufferSize: 1, refCount: false })),
            );
          }

          return attempts.get(file);
        }),
      ).pipe(map(FileValidators.concatenateErrors), defaultIfEmpty(null));
  }

  private requestUpload(request: FileUploadRequest): (file: File) => Observable<MessageValidationErrors | null> {
    return (file: File) =>
      request(file).pipe(
        filter((event) => [HttpEventType.UploadProgress, HttpEventType.Response].includes(event.type)),
        tap({
          next: (event) => {
            switch (event.type) {
              case HttpEventType.UploadProgress:
                this.uploadProgressSubject.next({ progress: event.loaded / event.total, file });
                break;
              case HttpEventType.Response:
                this.uploadProgressSubject.next({ progress: 1, file, uuid: event.body.uuid });
                break;
            }
          },
          error: (error: HttpErrorResponse) =>
            this.uploadProgressSubject.next({
              progress: null,
              file,
              errors: this.createValidationError(file, error),
            }),
        }),
        filter((event) => event.type === HttpEventType.Response),
        map((): null => null),
        catchError((error: HttpErrorResponse) => of(this.createValidationError(file, error))),
      );
  }

  private isFileAlreadyUploaded({ file, uuid }: FileUploadEvent): boolean {
    return !(file instanceof File) || !!uuid;
  }

  private isFileEmpty({ file }: FileUploadEvent): boolean {
    return !(file instanceof File) || file.size === 0;
  }

  private isFileLarge({ file }: FileUploadEvent): boolean {
    return !(file instanceof File) || file.size > MAX_FILE_SIZE_BYTES;
  }

  private createValidationError(file: File, error: HttpErrorResponse): MessageValidationErrors {
    return { upload: `${file.name} ${error.error?.message ?? error.message}` };
  }
}
