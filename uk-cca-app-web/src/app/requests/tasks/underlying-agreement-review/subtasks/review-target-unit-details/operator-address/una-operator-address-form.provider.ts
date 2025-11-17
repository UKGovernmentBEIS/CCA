import { InjectionToken, Provider } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';

export const UNA_OPERATOR_ADDRESS_FORM = new InjectionToken<FormGroup<AccountAddressFormModel>>(
  'Operator Address Form',
);

export const UnaOperatorAddressFormProvider: Provider = {
  provide: UNA_OPERATOR_ADDRESS_FORM,
  deps: [RequestTaskStore],
  useFactory: (store: RequestTaskStore) => {
    const addressPayload = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)()
      ?.operatorAddress;
    return createAccountAddressForm(addressPayload);
  },
};
