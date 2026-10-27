import { InjectionToken, Provider } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';

import { underlyingAgreementQuery } from '../+state/underlying-agreement.selectors';

export const OPERATOR_ADDRESS_FORM = new InjectionToken<AccountAddressFormModel>('Operator Address Form');

export const OperatorAddressFormProvider: Provider = {
  provide: OPERATOR_ADDRESS_FORM,
  deps: [RequestTaskStore],
  useFactory: (store: RequestTaskStore) => {
    const addressPayload = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)()
      ?.operatorAddress;
    return createAccountAddressForm(addressPayload);
  },
};
