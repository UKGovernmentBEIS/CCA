import { Injectable } from '@angular/core';

import { TaskService } from '@netz/common/forms';

import { CcaDecisionNotification, UnderlyingAgreementVariationActivationRequestTaskPayload } from 'cca-api';

import { underlyingAgreementVariationActivationQuery } from '../+state/una-variation-activation.selectors';
import { UnderlyingAgreementVariationActivationTaskApiService } from './underlying-agreement-variation-activation-task-api.service';

@Injectable()
export class UnderlyingAgreementVariationActivationTaskService extends TaskService {
  get payload(): UnderlyingAgreementVariationActivationRequestTaskPayload {
    return this.store.select(underlyingAgreementVariationActivationQuery.selectPayload)();
  }

  set payload(payload: UnderlyingAgreementVariationActivationRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementVariationActivationTaskApiService).notifyOperator(
      notificationPayload,
    );
  }
}
