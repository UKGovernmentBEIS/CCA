import { FormArray, FormControl } from '@angular/forms';

import { TargetUnitAccountPayload } from 'cca-api';

export type CompanyRegistrationNumberFormModel = {
  isCompanyRegistrationNumber: FormControl<TargetUnitAccountPayload['isCompanyRegistrationNumber']>;
  companyRegistrationNumber: FormControl<TargetUnitAccountPayload['companyRegistrationNumber']>;
  registrationNumberMissingReason: FormControl<TargetUnitAccountPayload['registrationNumberMissingReason']>;
};

export type TargetUnitCreationFormModel = {
  operatorType: FormControl<TargetUnitAccountPayload['operatorType']>;
  name: FormControl<TargetUnitAccountPayload['name']>;
  sicCodes: FormArray<FormControl<string>>;
  subsectorAssociationId?: FormControl<TargetUnitAccountPayload['subsectorAssociationId']>;
};
