import { FormControl } from '@angular/forms';

import { MessageValidationErrors, MessageValidatorFn } from '@netz/govuk-components';

import { FileType } from '../file-input/file-type.enum';
import { FileValidators } from './file-validators';

describe('FileValidators', () => {
  const createFileEvent = (
    overrides: Partial<{
      name: string;
      size: number;
      type: string;
      dimensions: { width: number; height: number };
    }> = {},
  ) => ({
    file: {
      name: overrides.name ?? 'Test file',
      size: overrides.size ?? 1024,
      type: overrides.type ?? FileType.PDF,
    },
    ...(overrides.dimensions ? { dimensions: overrides.dimensions } : {}),
  });

  it('should create an instance', () => {
    expect(new FileValidators()).toBeTruthy();
  });

  describe('concatenateErrors', () => {
    it('should concatenate the errors of several files', () => {
      const errors: MessageValidationErrors[] = [
        {
          upload: 'An upload has failed',
          test: 'A test has failed',
          random: 'A random error',
        },
        {
          upload: 'Another upload has failed',
        },
      ];

      expect(FileValidators.concatenateErrors(errors)).toEqual({
        'upload-0': 'An upload has failed',
        'test-0': 'A test has failed',
        'random-0': 'A random error',
        'upload-1': 'Another upload has failed',
      });
    });

    it('should return null when all errors are null', () => {
      expect(FileValidators.concatenateErrors([null, null])).toBeNull();
    });

    it('should ignore null errors', () => {
      expect(FileValidators.concatenateErrors([null, { upload: 'An upload has failed' }, null])).toEqual({
        'upload-1': 'An upload has failed',
      });
    });

    it('should return null for an empty array', () => {
      expect(FileValidators.concatenateErrors([])).toBeNull();
    });
  });

  describe('multipleCompose', () => {
    it('should apply a single file validator to multiple files', () => {
      const validator: MessageValidatorFn = (control) =>
        control.value.uuid ? null : { required: 'The file is required' };

      const formControl = new FormControl();

      formControl.setValue([{ uuid: 1 }, { uuid: null }]);

      expect(FileValidators.multipleCompose(validator)(formControl)).toEqual({
        'required-1': 'The file is required',
      });
    });

    it('should return null when all files pass validation', () => {
      const validator: MessageValidatorFn = (control) =>
        control.value.uuid ? null : { required: 'The file is required' };

      const formControl = new FormControl([{ uuid: 1 }, { uuid: 2 }]);

      expect(FileValidators.multipleCompose(validator)(formControl)).toBeNull();
    });

    it('should return null when there are no files', () => {
      const validator: MessageValidatorFn = () => ({
        required: 'The file is required',
      });

      const formControl = new FormControl([]);

      expect(FileValidators.multipleCompose(validator)(formControl)).toBeNull();
    });
  });

  describe('maxFileSize', () => {
    it('should allow a file smaller than the maximum size', () => {
      const formControl = new FormControl(
        createFileEvent({
          size: 1 * 1024 * 1024,
        }),
        {
          validators: FileValidators.maxFileSize(2),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should allow a file exactly at the maximum size', () => {
      const formControl = new FormControl(
        createFileEvent({
          size: 2 * 1024 * 1024,
        }),
        {
          validators: FileValidators.maxFileSize(2),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should reject a file larger than the maximum size', () => {
      const formControl = new FormControl(
        createFileEvent({
          size: 3 * 1024 * 1024,
        }),
        {
          validators: FileValidators.maxFileSize(2),
        },
      );

      expect(formControl.errors).toEqual({
        'maxFileSize-0': 'Test file must be 2MB or smaller',
      });
      expect(formControl.invalid).toBeTruthy();
    });

    it('should support a custom error message', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'large.pdf',
          size: 3 * 1024 * 1024,
        }),
        {
          validators: FileValidators.maxFileSize(2, 'is too large'),
        },
      );

      expect(formControl.errors).toEqual({
        'maxFileSize-0': 'large.pdf is too large',
      });
    });

    it('should validate multiple files', () => {
      const formControl = new FormControl(
        [
          createFileEvent({
            name: 'small.pdf',
            size: 1 * 1024 * 1024,
          }),
          createFileEvent({
            name: 'large.pdf',
            size: 3 * 1024 * 1024,
          }),
        ],
        {
          validators: FileValidators.maxFileSize(2),
        },
      );

      expect(formControl.errors).toEqual({
        'maxFileSize-1': 'large.pdf must be 2MB or smaller',
      });
    });
  });

  describe('notEmpty', () => {
    it('should allow a non-empty file', () => {
      const formControl = new FormControl(createFileEvent({ size: 1 }), {
        validators: FileValidators.notEmpty(),
      });

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should reject an empty file', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'empty.pdf',
          size: 0,
        }),
        {
          validators: FileValidators.notEmpty(),
        },
      );

      expect(formControl.errors).toEqual({
        'notEmpty-0': 'empty.pdf should not be empty',
      });
      expect(formControl.invalid).toBeTruthy();
    });

    it('should validate multiple files', () => {
      const formControl = new FormControl(
        [
          createFileEvent({
            name: 'valid.pdf',
            size: 100,
          }),
          createFileEvent({
            name: 'empty.pdf',
            size: 0,
          }),
        ],
        {
          validators: FileValidators.notEmpty(),
        },
      );

      expect(formControl.errors).toEqual({
        'notEmpty-1': 'empty.pdf should not be empty',
      });
    });
  });

  describe('validContentTypes', () => {
    const acceptedTypes = [FileType.PDF, FileType.DOCX, FileType.ZIP];

    it('should allow an accepted MIME type', () => {
      const formControl = new FormControl(
        createFileEvent({
          type: FileType.PDF,
        }),
        {
          validators: FileValidators.validContentTypes(acceptedTypes),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should reject an unaccepted MIME type', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'image.png',
          type: FileType.PNG,
        }),
        {
          validators: FileValidators.validContentTypes(acceptedTypes),
        },
      );

      expect(formControl.errors).toEqual({
        'validContentTypes-0': 'image.png has an invalid type',
      });
      expect(formControl.invalid).toBeTruthy();
    });

    it('should allow a file with no MIME type (e.g. existing attachments)', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'unknown-file',
          type: '',
        }),
        {
          validators: FileValidators.validContentTypes(acceptedTypes),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should accept application/zip', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'archive.zip',
          type: 'application/zip',
        }),
        {
          validators: FileValidators.validContentTypes([FileType.ZIP]),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should accept application/x-zip-compressed as a ZIP alias', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'archive.zip',
          type: 'application/x-zip-compressed',
        }),
        {
          validators: FileValidators.validContentTypes([FileType.ZIP]),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should validate multiple files', () => {
      const formControl = new FormControl(
        [
          createFileEvent({
            name: 'valid.pdf',
            type: FileType.PDF,
          }),
          createFileEvent({
            name: 'invalid.exe',
            type: 'application/octet-stream',
          }),
        ],
        {
          validators: FileValidators.validContentTypes([FileType.PDF]),
        },
      );

      expect(formControl.errors).toEqual({
        'validContentTypes-1': 'invalid.exe has an invalid type',
      });
    });

    it('should support a custom error message', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'file.exe',
          type: 'application/octet-stream',
        }),
        {
          validators: FileValidators.validContentTypes([FileType.PDF], 'is not a supported file type'),
        },
      );

      expect(formControl.errors).toEqual({
        'validContentTypes-0': 'file.exe is not a supported file type',
      });
    });
  });

  describe('maxImageDimensionsSize', () => {
    it('should allow an image within the maximum dimensions', () => {
      const formControl = new FormControl(
        createFileEvent({
          dimensions: {
            width: 1000,
            height: 800,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toBeNull();
      expect(formControl.valid).toBeTruthy();
    });

    it('should allow an image exactly at the maximum dimensions', () => {
      const formControl = new FormControl(
        createFileEvent({
          dimensions: {
            width: 1200,
            height: 1000,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toBeNull();
    });

    it('should reject an image wider than the maximum width', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'wide.jpg',
          dimensions: {
            width: 1201,
            height: 1000,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toEqual({
        'dimensions-0': 'wide.jpg must be smaller than 1200x1000 px',
      });
    });

    it('should reject an image taller than the maximum height', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'tall.jpg',
          dimensions: {
            width: 1200,
            height: 1001,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toEqual({
        'dimensions-0': 'tall.jpg must be smaller than 1200x1000 px',
      });
    });

    it('should reject an image exceeding both dimensions', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'large.jpg',
          dimensions: {
            width: 2000,
            height: 2000,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toEqual({
        'dimensions-0': 'large.jpg must be smaller than 1200x1000 px',
      });
    });

    it('should support a custom error message', () => {
      const formControl = new FormControl(
        createFileEvent({
          name: 'large.jpg',
          dimensions: {
            width: 2000,
            height: 2000,
          },
        }),
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000, 'is too large'),
        },
      );

      expect(formControl.errors).toEqual({
        'dimensions-0': 'large.jpg is too large',
      });
    });

    it('should allow a file when dimensions are not available', () => {
      const formControl = new FormControl(createFileEvent(), {
        validators: FileValidators.maxImageDimensionsSize(1200, 1000),
      });

      expect(formControl.errors).toBeNull();
    });

    it('should validate multiple files', () => {
      const formControl = new FormControl(
        [
          createFileEvent({
            name: 'small.jpg',
            dimensions: {
              width: 500,
              height: 500,
            },
          }),
          createFileEvent({
            name: 'large.jpg',
            dimensions: {
              width: 2000,
              height: 2000,
            },
          }),
        ],
        {
          validators: FileValidators.maxImageDimensionsSize(1200, 1000),
        },
      );

      expect(formControl.errors).toEqual({
        'dimensions-1': 'large.jpg must be smaller than 1200x1000 px',
      });
    });
  });
});
