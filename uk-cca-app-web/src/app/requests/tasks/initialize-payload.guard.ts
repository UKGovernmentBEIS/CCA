import { inject } from '@angular/core';

import { map, skipWhile, take, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { initializeAdminTerminationPayload } from './admin-termination/admin-termination-payload-initializer';
import { initializeAdminTerminationFinalDecisionPayload } from './admin-termination-final-decision/admin-termination-final-decision-payload-initializer';
import { initializeUnderlyingAgreementSubmitPayload } from './underlying-agreement-application/underlying-agreement-application-payload-initializer';
import { initializeUnderlyingAgreementVariationSubmitPayload } from './underlying-agreement-variation/underlying-agreement-variation-payload-initializer';
import { initializeWithdrawAdminTerminationPayload } from './withdraw-admin-termination/withdraw-admin-termination-payload-initializer';

export const initializePayloadGuard = () => {
  const store = inject(RequestTaskStore);
  return store.rxSelect(requestTaskQuery.selectRequestTask).pipe(
    skipWhile((t) => !t),
    take(1),
    tap((task) => {
      const type = task.type;
      const payload = task.payload;
      switch (type) {
        case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT':
          store.setPayload(initializeUnderlyingAgreementSubmitPayload(payload));
          break;

        case 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT':
          store.setPayload(initializeUnderlyingAgreementVariationSubmitPayload(payload));
          break;

        case 'ADMIN_TERMINATION_APPLICATION_SUBMIT':
          store.setPayload(initializeAdminTerminationPayload(payload));
          break;

        case 'ADMIN_TERMINATION_APPLICATION_WITHDRAW':
          store.setPayload(initializeWithdrawAdminTerminationPayload(payload));
          break;

        case 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION':
          store.setPayload(initializeAdminTerminationFinalDecisionPayload(payload));
          break;
      }
    }),
    map(() => true),
  );
};
