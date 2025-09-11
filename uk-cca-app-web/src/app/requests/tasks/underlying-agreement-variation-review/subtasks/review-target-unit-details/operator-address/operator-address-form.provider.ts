import { InjectionToken } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';

export const OPERATOR_ADDRESS_FORM = new InjectionToken<FormGroup<AccountAddressFormModel>>('Operator Address Form');

export const OperatorAddressFormProvider = {
  provide: OPERATOR_ADDRESS_FORM,
  deps: [RequestTaskStore],
  useFactory: (store: RequestTaskStore) => {
    const payload = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();
    return createAccountAddressForm(payload?.operatorAddress);
  },
};
