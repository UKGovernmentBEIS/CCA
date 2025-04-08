import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import {
  TargetUnitAccountContactDTO,
  TargetUnitAccountDetailsResponseDTO,
  UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO,
  UpdateTargetUnitAccountResponsiblePersonDTO,
  UpdateTargetUnitAccountSicCodeDTO,
} from 'cca-api';

const initialState: TargetUnitAccountDetailsResponseDTO = {
  subsectorAssociation: null,
  targetUnitAccountDetails: null,
  underlyingAgreementDetails: null,
};

@Injectable()
export class ActiveTargetUnitStore extends SignalStore<TargetUnitAccountDetailsResponseDTO> {
  constructor() {
    super(initialState);
  }

  updateTargetUnitAccountSicCode(dto: UpdateTargetUnitAccountSicCodeDTO) {
    this.updateState({
      targetUnitAccountDetails: { ...this.state.targetUnitAccountDetails, ...dto },
    });
  }

  updateFinancialIndependenceStatus(dto: UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO) {
    this.updateState({
      targetUnitAccountDetails: { ...this.state.targetUnitAccountDetails, ...dto },
    });
  }

  updateResponsiblePerson(dto: UpdateTargetUnitAccountResponsiblePersonDTO) {
    this.updateState({
      targetUnitAccountDetails: {
        ...this.state.targetUnitAccountDetails,
        responsiblePerson: { ...this.state.targetUnitAccountDetails.responsiblePerson, ...dto },
      },
    });
  }

  updateAdministrativeContact(dto: TargetUnitAccountContactDTO) {
    this.updateState({
      targetUnitAccountDetails: {
        ...this.state.targetUnitAccountDetails,
        administrativeContactDetails: { ...this.state.targetUnitAccountDetails.administrativeContactDetails, ...dto },
      },
    });
  }
}
