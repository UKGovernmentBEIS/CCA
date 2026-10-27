export type PatCsvUploadErrorCode = 'FPAT1004';

export const PAT_CSV_UPLOAD_ERROR_MESSAGES = {
  FPAT1004: 'The reporting period for the selected reporting year has expired - the workflow must be closed',
} satisfies Record<PatCsvUploadErrorCode, string>;
