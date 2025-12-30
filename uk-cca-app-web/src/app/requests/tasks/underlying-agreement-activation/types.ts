import {
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementActivationSaveRequestTaskActionPayload,
} from 'cca-api';

export type UnAActivationRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementActivationSaveRequestTaskActionPayload;
};

export type UnAActivationNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};
