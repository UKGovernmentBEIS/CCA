import { FormGroup } from '@angular/forms';

import { AccountAddressFormModel } from '@shared/components';

import { TargetUnitAccountContactDTO } from 'cca-api';

export type FormControlConfig<T> = {
  value: T;
  disabled: boolean;
};

export type ResponsiblePersonFormConfig = {
  email: FormControlConfig<TargetUnitAccountContactDTO['email']>;
  firstName: FormControlConfig<TargetUnitAccountContactDTO['firstName']>;
  lastName: FormControlConfig<TargetUnitAccountContactDTO['lastName']>;
  jobTitle: FormControlConfig<TargetUnitAccountContactDTO['jobTitle']>;
  phoneNumber: FormControlConfig<TargetUnitAccountContactDTO['phoneNumber']>;
  address: FormControlConfig<FormGroup<AccountAddressFormModel>>;
  sameAddress?: FormControlConfig<boolean[]>;
};
