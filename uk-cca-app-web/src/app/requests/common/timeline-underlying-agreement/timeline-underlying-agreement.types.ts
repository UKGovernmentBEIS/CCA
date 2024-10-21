import {
  UnderlyingAgreementAcceptedRequestActionPayload,
  UnderlyingAgreementPayload,
  UnderlyingAgreementRejectedRequestActionPayload,
  UnderlyingAgreementSubmittedRequestActionPayload,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationSubmittedRequestActionPayload,
} from 'cca-api';

export type UnderlyingAgreementDecisionRequestActionPayload =
  | UnderlyingAgreementAcceptedRequestActionPayload
  | UnderlyingAgreementRejectedRequestActionPayload;

export type UnderlyingAgreementRequestActionPayload =
  | UnderlyingAgreementSubmittedRequestActionPayload
  | UnderlyingAgreementDecisionRequestActionPayload
  | UnderlyingAgreementVariationSubmittedRequestActionPayload;

export type UnaPayload = UnderlyingAgreementPayload | UnderlyingAgreementVariationPayload;
