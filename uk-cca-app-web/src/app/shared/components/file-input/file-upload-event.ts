import { MessageValidationErrors } from '@netz/govuk-components';

/**
 * Reference to a file that is already stored on the server.
 * Only `name` is guaranteed; `size` and `type` are absent for
 * pseudo-file references built from stored attachment metadata.
 */
export interface UploadedFileRef {
  name: string;
  size?: number;
  type?: string;
}

export interface FileUploadEvent {
  downloadUrl?: string | string[];
  errors?: MessageValidationErrors;
  file: File | UploadedFileRef;
  progress?: number;
  uuid?: string;
  dimensions?: {
    width: number;
    height: number;
  };
}

export type FileUpload = Pick<FileUploadEvent, 'file' | 'uuid' | 'dimensions'>;
export type UuidFilePair = { file: File | UploadedFileRef; uuid: string };

export const UNKNOWN_FILE_NAME = 'File';

export const storedFileName = (uuid: string, attachments?: Record<string, string>): string => attachments?.[uuid] || '';
