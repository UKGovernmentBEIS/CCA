import { AbstractControl, AsyncValidatorFn } from '@angular/forms';

import { of } from 'rxjs';

import { GovukValidators, MessageValidationErrors, MessageValidatorFn } from '@netz/govuk-components';

import { FileType, FileTypeAliases } from '../file-input/file-type.enum';
import { FileUploadEvent } from '../file-input/file-upload-event';

export class FileValidators {
  private static getValues(value: FileUploadEvent | FileUploadEvent[]): FileUploadEvent[] {
    if (!value) return [];
    return (Array.isArray(value) ? value : [value]).filter(Boolean);
  }

  static maxFileSize(max: number, message?: string): MessageValidatorFn {
    const maxBytes = max * 1024 * 1024;

    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const messages: MessageValidationErrors = {};

      this.getValues(value).forEach((event, index) => {
        const file = event?.file;

        if (file?.size && file.size > maxBytes) {
          messages[`maxFileSize-${index}`] = `${file.name} ${message ?? `must be ${max}MB or smaller`}`;
        }
      });

      return Object.keys(messages).length ? messages : null;
    };
  }

  static notEmpty(): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const messages: MessageValidationErrors = {};

      this.getValues(value).forEach((event, index) => {
        const file = event?.file;

        if (file?.size === 0) {
          messages[`notEmpty-${index}`] = `${file.name} should not be empty`;
        }
      });

      return Object.keys(messages).length ? messages : null;
    };
  }

  static validContentTypes(types: FileType[], message = 'has an invalid type'): MessageValidatorFn {
    const acceptedTypes = new Set(types.flatMap((type) => [type, ...(FileTypeAliases[type] ?? [])]));

    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const messages: MessageValidationErrors = {};

      this.getValues(value).forEach((event, index) => {
        const file = event?.file;
        const fileType = file?.type;

        if (fileType && !acceptedTypes.has(fileType as FileType)) {
          messages[`validContentTypes-${index}`] = `${file?.name ?? 'File'} ${message}`;
        }
      });

      return Object.keys(messages).length ? messages : null;
    };
  }

  static maxImageDimensionsSize(maxWidth: number, maxHeight: number, message?: string): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const messages: MessageValidationErrors = {};

      this.getValues(value).forEach((event, index) => {
        const dimensions = event?.dimensions;
        if (!dimensions || (!dimensions.width && !dimensions.height)) return;

        const isInvalid = dimensions.width > maxWidth || dimensions.height > maxHeight;

        if (isInvalid) {
          messages[`dimensions-${index}`] = `${event.file.name} ${
            message ?? `must be smaller than ${maxWidth}x${maxHeight} px`
          }`;
        }
      });

      return Object.keys(messages).length ? messages : null;
    };
  }

  static concatenateErrors(errors: MessageValidationErrors[] = []): MessageValidationErrors {
    if (errors.every((error) => error === null)) return null;

    return errors.reduce<MessageValidationErrors>((result, error, index) => {
      if (!error) return result;

      Object.entries(error).forEach(([key, value]) => {
        result[`${key}-${index}`] = value;
      });

      return result;
    }, {});
  }

  static multipleCompose(
    validator: ({ value }: { value: FileUploadEvent }) => MessageValidationErrors,
  ): MessageValidatorFn {
    return (control) => {
      const value: FileUploadEvent[] | null = control.value;
      if (!value?.length) return null;

      const fileValidities = value.map((fileEvent) => validator({ value: fileEvent }));
      return this.concatenateErrors(fileValidities);
    };
  }
}

const commonAcceptedFileTypes = [
  FileType.PDF,
  FileType.DOCX,
  FileType.DOC,
  FileType.XLSX,
  FileType.XLS,
  FileType.PPTX,
  FileType.PPT,
  FileType.VSDX,
  FileType.VSD,
  FileType.JPEG,
  FileType.JPG,
  FileType.PNG,
  FileType.DIB,
  FileType.BMP,
  FileType.TIFF,
  FileType.CSV,
  FileType.TXT,
  FileType.ZIP,
];

export const MAX_FILE_SIZE_MB = 20;
export const MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;

export const commonFileValidators: MessageValidatorFn[] = [
  FileValidators.maxFileSize(MAX_FILE_SIZE_MB),
  FileValidators.notEmpty(),
  FileValidators.validContentTypes(commonAcceptedFileTypes),
];

export const requiredFileValidator = GovukValidators.required('Select a file');

/**
 * Converts synchronous file validators to asynchronous validators
 * so that file upload can run even when there are validation errors.
 */
export const createCommonFileAsyncValidators = (isRequired: boolean): AsyncValidatorFn[] => {
  const validators = isRequired ? [...commonFileValidators, requiredFileValidator] : commonFileValidators;
  return validators.map((validator) => (control: AbstractControl) => of(validator(control)));
};
