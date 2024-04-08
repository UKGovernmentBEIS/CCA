import { KeycloakProfile } from 'keycloak-js';

import { RequestInfoDTO, RequestTaskDTO } from 'cca-api';

import { PaymentState } from '../store/payment.state';

export interface PaymentDetails {
  amount?: number;
  paidByFullName?: string;
  paymentDate?: string;
  paymentMethod?: 'BANK_TRANSFER' | 'CREDIT_OR_DEBIT_CARD';
  paymentRefNum?: string;
  receivedDate?: string;
  status?: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED';
}

export function mapMakePaymentToPaymentDetails(userProfile: KeycloakProfile, state: PaymentState): PaymentDetails {
  return {
    amount: +state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'BANK_TRANSFER',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: state.markedAsPaid ? 'MARK_AS_PAID' : null,
  };
}

export function mapGOVUKToPaymentDetails(
  userProfile: KeycloakProfile,
  state: PaymentState,
  status: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED',
): PaymentDetails {
  return {
    amount: +state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'CREDIT_OR_DEBIT_CARD',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: status,
  };
}

export function mapTrackPaymentToPaymentDetails(state: PaymentState): PaymentDetails {
  return [
    'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
    'PERMIT_SURRENDER_CONFIRM_PAYMENT',
    'PERMIT_REVOCATION_CONFIRM_PAYMENT',
    'PERMIT_VARIATION_CONFIRM_PAYMENT',
    'PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT',
    'PERMIT_TRANSFER_A_CONFIRM_PAYMENT',
    'PERMIT_TRANSFER_B_CONFIRM_PAYMENT',
    'DRE_CONFIRM_PAYMENT',
    'EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT',
    'AVIATION_DRE_UKETS_CONFIRM_PAYMENT',
    'EMP_VARIATION_UKETS_CONFIRM_PAYMENT',
    'EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT',
  ].includes(state.requestTaskItem.requestTask.type)
    ? { ...state.paymentDetails, amount: +state.paymentDetails.amount }
    : {
        amount: +state.paymentDetails.amount,
        paymentRefNum: state.paymentDetails.paymentRefNum,
        status: null,
      };
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function getHeadingMap(year?: number): Partial<Record<RequestTaskDTO['type'], string>> {
  return {};
}

export const paymentHintInfo: Partial<Record<RequestTaskDTO['type'], string>> = {};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function trackShouldDisplayMarkPaidConfirmationInfo(requestType: RequestInfoDTO['type']): boolean {
  return true;
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function trackShouldDisplayCancelHintInfo(requestType: RequestInfoDTO['type']): boolean {
  return true;
}
