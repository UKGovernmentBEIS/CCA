import {
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload,
} from 'cca-api';

export type UnAVariationActivationRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
};

export type UnAVariationActivationNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};
