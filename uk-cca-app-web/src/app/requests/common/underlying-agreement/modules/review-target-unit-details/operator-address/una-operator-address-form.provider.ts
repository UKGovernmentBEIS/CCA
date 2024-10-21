import { InjectionToken, Provider } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';

import { underlyingAgreementQuery } from '../../../+state';

export const UNA_OPERATOR_ADDRESS_FORM = new InjectionToken<AccountAddressFormModel>('Operator Address Form');

export const UnaOperatorAddressFormProvider: Provider = {
  provide: UNA_OPERATOR_ADDRESS_FORM,
  deps: [RequestTaskStore],
  useFactory: (store: RequestTaskStore) => {
    const addressPayload = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)()
      ?.operatorAddress;
    return createAccountAddressForm(addressPayload);
  },
};
