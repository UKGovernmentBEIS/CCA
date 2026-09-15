export enum FileType {
  DOC = 'application/msword',
  DOCX = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',

  XLS = 'application/vnd.ms-excel',
  XLSX = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',

  PPT = 'application/vnd.ms-powerpoint',
  PPTX = 'application/vnd.openxmlformats-officedocument.presentationml.presentation',

  VSD = 'application/vnd.visio',
  VSDX = 'application/vnd.ms-visio.viewer',

  PDF = 'application/pdf',

  JPG = 'image/jpg',
  JPEG = 'image/jpeg',
  PNG = 'image/png',
  DIB = 'image/dib',
  BMP = 'image/bmp',
  TIFF = 'image/tiff',

  TXT = 'text/plain',
  CSV = 'text/csv',

  ZIP = 'application/zip',
}

/**
 * MIME types that represent the same logical file type.
 *
 * Browsers/OSes can report ZIP files using either of these values.
 */
export const FileTypeAliases: Partial<Record<FileType, readonly string[]>> = {
  [FileType.ZIP]: [FileType.ZIP, 'application/x-zip-compressed'],
};
