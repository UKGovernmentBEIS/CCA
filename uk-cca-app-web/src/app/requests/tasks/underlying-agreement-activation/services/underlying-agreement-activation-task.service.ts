import { Injectable } from '@angular/core';

import { TaskService } from '@netz/common/forms';

import { CcaDecisionNotification } from 'cca-api';

import { underlyingAgreementActivationQuery } from '../+state/una-activation.selectors';
import { UNAActivationRequestTaskPayload } from '../underlying-agreement-activation.types';
import { UnderlyingAgreementActivationTaskApiService } from './underlying-agreement-activation-task-api.service';

@Injectable()
export class UnderlyingAgreementActivationTaskService extends TaskService {
  get payload(): UNAActivationRequestTaskPayload {
    return this.store.select(underlyingAgreementActivationQuery.selectPayload)();
  }

  set payload(payload: UNAActivationRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementActivationTaskApiService).notifyOperator(notificationPayload);
  }
}
