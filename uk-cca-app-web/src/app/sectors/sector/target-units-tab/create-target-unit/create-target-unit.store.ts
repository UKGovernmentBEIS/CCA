import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { TargetUnitAccountCreationSubmitApplicationCreateActionPayload, TargetUnitAccountPayload } from 'cca-api';

const INITIAL_STATE: TargetUnitAccountPayload = {
  name: null,
  emissionTradingScheme: 'DUMMY_EMISSION_TRADING_SCHEME', // TODO: replace with actual value
  competentAuthority: null,
  operatorType: null,
  companyRegistrationNumber: null,
  registrationNumberMissingReason: null,
  sicCodes: null,
  subsectorAssociationId: null,
  address: null,
  responsiblePerson: null,
  administrativeContactDetails: null,
  isCompanyRegistrationNumber: null,
};

@Injectable()
export class CreateTargetUnitStore extends SignalStore<TargetUnitAccountPayload> {
  sameAddressWithOperator = false;
  sameAddressWithResponsiblePerson = false;

  constructor() {
    super(INITIAL_STATE);
  }

  getSubmitPayload(): TargetUnitAccountCreationSubmitApplicationCreateActionPayload {
    return {
      payloadType: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD',
      payload: { ...this.state },
    };
  }
}
