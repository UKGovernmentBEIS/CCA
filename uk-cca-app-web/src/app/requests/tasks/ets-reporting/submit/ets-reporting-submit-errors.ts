import { ErrorSummaryInfo } from '@shared/components';

export type EtsSubmitErrorCode = 'ETS1004' | 'ETS1005';

export const ETS_SUBMIT_ERROR_MESSAGES = {
  ETS1004: {
    message:
      'Some of the information in your UK ETS report could not be validated. Check your energy/fuel details and try again.',
    link: '../energy-fuel-amount/check-your-answers',
    linkText: 'Check your UK ETS energy/fuel details',
  },
  ETS1005: {
    message:
      'The calculated totals in your UK ETS report could not be validated. Check your energy/fuel details and try again.',
    link: '../energy-fuel-amount/check-your-answers',
    linkText: 'Check your UK ETS energy/fuel details',
  },
} satisfies Record<EtsSubmitErrorCode, ErrorSummaryInfo>;

export const ETS_SUBMIT_UNEXPECTED_ERROR: ErrorSummaryInfo = {
  message: 'Your UK ETS report could not be submitted. Please try again.',
};
