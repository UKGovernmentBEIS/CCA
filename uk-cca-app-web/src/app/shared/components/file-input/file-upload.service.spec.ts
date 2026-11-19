import { HttpErrorResponse, HttpEvent, HttpEventType, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';

import { merge, Observable } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { testSchedulerFactory } from '@netz/common/testing';
import { MessageValidationErrors } from '@netz/govuk-components';

import { FileUuidDTO } from 'cca-api';

import { FileUploadService } from './file-upload.service';

describe('FileUploadService', () => {
  let service: FileUploadService;
  let testScheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileUploadService);
    testScheduler = testSchedulerFactory();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit validation error for a single file upload', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const input$ = cold<HttpEvent<FileUuidDTO>>('--a-b--c|', {
        a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
        b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
        c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
      });

      const file = new File(['some content'], 'file.txt');

      expectObservable(
        service.upload(() => input$)(new FormControl({ file })) as Observable<MessageValidationErrors>,
      ).toBe('-------c|', { c: null });
      expectObservable(service.uploadProgress$).toBe('--a-b--c', {
        a: { file, progress: 5 / 15 },
        b: { file, progress: 10 / 15 },
        c: { file, progress: 1, uuid: 'abcd' },
      });
    });
  });

  it('should emit validation error for multiple files upload', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const fileA = new File(['some content'], 'file.txt');
      const fileB = new File(['some other content'], 'file2.txt');
      const fileC = new File(['some uploaded content'], 'file3.txt');
      const control = new FormControl([{ file: fileA }, { file: fileB }, { file: fileC, uuid: 'abcd' }]);

      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a---b------c|', {
          a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
          b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
          c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.uploadMany(upload$)(control) as Observable<MessageValidationErrors>).toBe(
        '--------------(c|)',
        { c: null },
      );

      expectObservable(service.uploadProgress$).toBe('--(aa)(bb)---(cc)', {
        a: { file: fileA, progress: 5 / 15 },
        b: { file: fileB, progress: 10 / 15 },
        c: { file: fileA, progress: 1, uuid: 'abcd' },
      });
    });
  });

  it('should be valid when no file is attached', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const control = new FormControl(null);
      const request = vi.fn();

      expectObservable(service.upload(request)(control) as Observable<MessageValidationErrors>).toBe('(c|)', {
        c: null,
      });

      expectObservable(service.uploadProgress$).toBe('--');

      flush();

      expect(request).not.toHaveBeenCalled();
    });
  });

  it('should be valid when no files are attached', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const control = new FormControl([]);
      const request = vi.fn();

      expectObservable(service.uploadMany(request)(control) as Observable<MessageValidationErrors>).toBe('(c|)', {
        c: null,
      });

      expectObservable(service.uploadProgress$).toBe('----');

      flush();

      expect(request).not.toHaveBeenCalled();
    });
  });

  // The value shape must not be assumed: a scalar handed to the multiple files validator used to throw
  // from `value.map`, and must still be uploaded.
  it('should upload a single entry handed to the multiple files validator', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const file = new File(['content'], 'single-entry.txt');
      const control = new FormControl({ file });
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.uploadMany(upload$)(control) as Observable<MessageValidationErrors>).toBe('---(a|)', {
        a: null,
      });

      flush();

      expect(upload$).toHaveBeenCalledWith(file);
    });
  });

  // The single file validator must tolerate a list value: only the first entry that still has a file to
  // upload may be uploaded, and only once.
  it('should upload the pending file of a list handed to the single file validator', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const uploaded = new File(['content'], 'uploaded.txt');
      const pending = new File(['content'], 'pending.txt');
      const control = new FormControl([{ file: uploaded, uuid: 'abcd' }, { uuid: 'efgh' }, { file: pending }]);
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'ijkl' } }),
        }),
      );

      expectObservable(service.upload(upload$)(control) as Observable<MessageValidationErrors>).toBe('--a|', {
        a: null,
      });

      flush();

      expect(upload$).toHaveBeenCalledTimes(1);
      expect(upload$).toHaveBeenCalledWith(pending);
    });
  });

  it('should upload a single file exactly at the maximum size', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const file = new File(['content'], 'at-limit.txt');
      vi.spyOn(file, 'size', 'get').mockReturnValue(20 * 1024 * 1024);
      const control = new FormControl({ file });
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.upload(upload$)(control) as Observable<MessageValidationErrors>).toBe('--a|', {
        a: null,
      });

      flush();

      expect(upload$).toHaveBeenCalledWith(file);
    });
  });

  it('should not upload a single file larger than the maximum size', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const file = new File(['content'], 'too-large.txt');
      vi.spyOn(file, 'size', 'get').mockReturnValue(20 * 1024 * 1024 + 1);
      const control = new FormControl({ file });
      const upload$ = vi.fn();

      expectObservable(service.upload(upload$)(control) as Observable<MessageValidationErrors>).toBe('(a|)', {
        a: null,
      });

      flush();

      expect(upload$).not.toHaveBeenCalled();
    });
  });

  it('should upload a file exactly at the maximum size among multiple files', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const file = new File(['content'], 'at-limit.txt');
      vi.spyOn(file, 'size', 'get').mockReturnValue(20 * 1024 * 1024);
      const control = new FormControl([{ file }]);
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.uploadMany(upload$)(control) as Observable<MessageValidationErrors>).toBe('---(a|)', {
        a: null,
      });

      flush();

      expect(upload$).toHaveBeenCalledWith(file);
    });
  });

  it('should not upload files larger than the maximum size among multiple files', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const fileA = new File(['content'], 'valid.txt');
      const fileB = new File(['content'], 'too-large.txt');
      vi.spyOn(fileB, 'size', 'get').mockReturnValue(20 * 1024 * 1024 + 1);
      const control = new FormControl([{ file: fileA }, { file: fileB }]);
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.uploadMany(upload$)(control) as Observable<MessageValidationErrors>).toBe('---(a|)', {
        a: null,
      });

      flush();

      expect(upload$).toHaveBeenCalledTimes(1);
      expect(upload$).toHaveBeenCalledWith(fileA);
    });
  });

  it('should not upload an empty single file', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const file = new File([], 'empty.txt');
      const control = new FormControl({ file });
      const upload$ = vi.fn();

      expectObservable(service.upload(upload$)(control) as Observable<MessageValidationErrors>).toBe('(a|)', {
        a: null,
      });

      flush();

      expect(upload$).not.toHaveBeenCalled();
    });
  });

  it('should cache a single file upload attempt across validator runs', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      const file = new File(['content'], 'file.txt');
      const control = new FormControl({ file });
      const upload$ = vi.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a|', {
          a: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );
      const validator = service.upload(upload$);

      expectObservable(validator(control) as Observable<MessageValidationErrors>).toBe('--a|', { a: null });
      expectObservable(validator(control) as Observable<MessageValidationErrors>).toBe('--a|', { a: null });

      flush();

      expect(upload$).toHaveBeenCalledTimes(1);
    });
  });

  it('should emit file upload events from various validations', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const controlA = new FormControl({ file: new File(['some content'], 'single-file.txt') });

      const controlB = new FormControl([
        { file: new File(['first content'], 'first-file.txt') },
        { file: new File(['second content'], 'second-file.txt') },
      ]);

      const error = new HttpErrorResponse({
        status: HttpStatusCode.BadRequest,
        statusText: 'Bad request',
        error: { message: 'File upload failed' },
      });

      const upload$ = vi.fn((file: File) =>
        file.name === 'second-file.txt'
          ? cold<HttpEvent<FileUuidDTO>>(
              '--a----#',
              { a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 } },
              error,
            )
          : cold<HttpEvent<FileUuidDTO>>('--a----b------c|', {
              a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
              b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
              c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
            }),
      );

      expectObservable(
        merge(
          service.upload(upload$)(controlA),
          service.uploadMany(upload$)(controlB),
        ) as Observable<MessageValidationErrors>,
      ).toBe('--------------a(b|)', { a: null, b: { 'upload-1': `second-file.txt ${error.error.message}` } });

      expectObservable(service.uploadProgress$).toBe('--(abc)(def)--(gh)--', {
        a: { file: controlA.value.file, progress: 5 / 15 },
        b: { file: controlB.value[0].file, progress: 5 / 15 },
        c: { file: controlB.value[1].file, progress: 5 / 15 },
        d: { file: controlA.value.file, progress: 10 / 15 },
        e: { file: controlB.value[0].file, progress: 10 / 15 },
        f: {
          file: controlB.value[1].file,
          progress: null,
          errors: { upload: `second-file.txt ${error.error.message}` },
        },
        g: { file: controlA.value.file, progress: 1, uuid: 'abcd' },
        h: { file: controlB.value[0].file, progress: 1, uuid: 'abcd' },
      });
    });
  });
});
