import { RequestActionDTO } from 'cca-api';

import { PaymentState } from '../store/payment.state';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function shouldHidePaymentAmount(state: PaymentState): boolean {
  return false;
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function getPaymentBaseLink(requestType: RequestActionDTO['requestType']): string {
  return '';
}
