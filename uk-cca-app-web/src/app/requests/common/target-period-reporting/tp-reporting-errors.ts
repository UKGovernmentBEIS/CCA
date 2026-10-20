import { ErrorSummaryInfo } from '@shared/components';

export type TpReportingErrorCode = 'TPRDF1001' | 'TPRDF1002' | 'TPRDF1003' | 'TPRDF1004' | 'TPRDF1005' | 'TPRDF1009';
export type TpReportingSubmitErrorCode = 'TPRDF1002' | 'TPRDF1004' | 'TPRDF1005' | 'TPRDF1008';
export type TpReportingRefreshErrorCode = 'TPRDF1002' | 'TPRDF1004' | 'TPRDF1005' | 'TPRDF1008' | 'TPRDF1009';

export const TP_REPORTING_PRODUCTS_NOT_ELIGIBLE_MESSAGE =
  'The baseline data for this facility must contain at least one product with a base year equal to the facility base year, and at least one product with a base year less than or equal to the year the report data relates to. You must submit a variation to correct the base year of products in this facility before you can submit your report.';

export const TP_REPORTING_ERROR_MESSAGES = {
  TPRDF1001:
    'There is already a TPR task in progress for the target period you selected. You can locate the relevant task through the main dashboard.',
  TPRDF1002:
    'A target period report cannot be submitted against the target period you selected for this facility. Select a different target period or exit the task.',
  TPRDF1003: 'The combination you have selected has expired. Make a new selection.',
  TPRDF1004:
    'The reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request.',
  TPRDF1005: TP_REPORTING_PRODUCTS_NOT_ELIGIBLE_MESSAGE,
  TPRDF1009: 'There is an error in the facility’s baseline data. Please contact your regulator.',
} satisfies Record<TpReportingErrorCode, string>;

export const TP_REPORTING_SUBMIT_ERROR_MESSAGES = {
  TPRDF1002: {
    message: 'This facility is not eligible to report for this target period - the workflow must be cancelled.',
    link: '../../cancel',
    linkText: 'Go to cancel task',
  },
  TPRDF1004: {
    message: '',
    link: '',
    linkText:
      'The reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request.',
  },
  TPRDF1005: {
    message: TP_REPORTING_PRODUCTS_NOT_ELIGIBLE_MESSAGE,
    link: '../../cancel',
    linkText: 'Go to cancel task',
  },
  TPRDF1008: {
    message:
      'A newer version of the baseline data is available. You need to refresh and reconfirm all information before submitting the TPR.',
    link: '../refresh-baseline-data',
    linkText: 'Go to refresh baseline data page',
  },
} satisfies Record<TpReportingSubmitErrorCode, ErrorSummaryInfo>;

export const TP_REPORTING_REFRESH_ERROR_MESSAGES = {
  TPRDF1002: {
    message: 'This facility is not eligible to report for this target period - the workflow must be cancelled.',
    link: '../../cancel',
    linkText: 'Go to cancel task',
  },
  TPRDF1004: {
    message: '',
    link: '',
    linkText:
      'The reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request.',
  },
  TPRDF1005: {
    message: TP_REPORTING_PRODUCTS_NOT_ELIGIBLE_MESSAGE,
    link: '../../cancel',
    linkText: 'Go to cancel task',
  },
  TPRDF1008: {
    message: '',
    link: '.',
    linkText: 'An error occurred during the baseline refresh. Please contact your regulator.',
  },
  TPRDF1009: {
    message: '',
    link: '.',
    linkText: 'An error occurred during the baseline refresh. Please contact your regulator.',
  },
} satisfies Record<TpReportingRefreshErrorCode, ErrorSummaryInfo>;
