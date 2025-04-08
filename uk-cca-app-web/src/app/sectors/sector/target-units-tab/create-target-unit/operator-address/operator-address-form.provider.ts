import { InjectionToken, Provider } from '@angular/core';

import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';

import { CreateTargetUnitStore } from '../create-target-unit.store';

export const TARGET_UNIT_OPERATOR_ADDRESS_FORM = new InjectionToken<AccountAddressFormModel>(
  'Target Unit Operator Address Form',
);

export const TargetUnitOperatorAddressFormProvider: Provider = {
  provide: TARGET_UNIT_OPERATOR_ADDRESS_FORM,
  deps: [CreateTargetUnitStore],
  useFactory: (createTargetUnitStore: CreateTargetUnitStore) => {
    const addressPayload = createTargetUnitStore.state.address;
    return createAccountAddressForm(addressPayload);
  },
};
