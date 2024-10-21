import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { CCAGovukValidators, textFieldValidators } from '@shared/validators';

import { SubsectorAssociationSchemeInfoDTO, TargetUnitAccountPayload } from 'cca-api';

export type TargetUnitCreationFormModel = {
  operatorType: FormControl<TargetUnitAccountPayload['operatorType']>;
  name: FormControl<TargetUnitAccountPayload['name']>;
  isCompanyRegistrationNumber: FormControl<TargetUnitAccountPayload['isCompanyRegistrationNumber']>;
  companyRegistrationNumber: FormControl<TargetUnitAccountPayload['companyRegistrationNumber']>;
  registrationNumberMissingReason: FormControl<TargetUnitAccountPayload['registrationNumberMissingReason']>;
  sicCode: FormControl<TargetUnitAccountPayload['sicCode']>;
  subsectorAssociationId?: FormControl<TargetUnitAccountPayload['subsectorAssociationId']>;
};

export function createTargetUnitAccountDetailsForm(
  fb: FormBuilder,
  targetUnitAccountDetails: TargetUnitAccountPayload,
  subSectors: SubsectorAssociationSchemeInfoDTO[],
  disabledForEdit?: boolean,
): FormGroup<TargetUnitCreationFormModel> {
  const group = fb.group<TargetUnitCreationFormModel>(
    {
      operatorType: fb.control(
        {
          value: targetUnitAccountDetails?.operatorType,
          disabled: disabledForEdit,
        },
        [GovukValidators.required('You must select an operator type')],
      ),
      name: fb.control(
        {
          value: targetUnitAccountDetails?.name,
          disabled: disabledForEdit,
        },
        textFieldValidators('operator name'),
      ),
      isCompanyRegistrationNumber: fb.control(
        {
          value: disabledForEdit
            ? !!targetUnitAccountDetails?.companyRegistrationNumber
            : targetUnitAccountDetails?.isCompanyRegistrationNumber !== false,
          disabled: disabledForEdit,
        },
        [GovukValidators.required('You must select an option')],
      ),
      companyRegistrationNumber: fb.control(
        {
          value: targetUnitAccountDetails?.companyRegistrationNumber,
          disabled: disabledForEdit,
        },
        textFieldValidators('registration number'),
      ),
      registrationNumberMissingReason: fb.control(
        {
          value: targetUnitAccountDetails?.registrationNumberMissingReason,
          disabled: disabledForEdit,
        },
        textFieldValidators('reason for not having registration number'),
      ),
      sicCode: fb.control(targetUnitAccountDetails?.sicCode, CCAGovukValidators.maxLength('SIC code')),
    },
    { updateOn: 'change' },
  );

  if (subSectors.length > 0) {
    group.addControl(
      'subsectorAssociationId',
      fb.control(
        {
          value: targetUnitAccountDetails?.subsectorAssociationId,
          disabled: disabledForEdit,
        },
        subSectors.length ? [GovukValidators.required('You must select a subsector')] : null,
      ),
    );
  } else {
    group.removeControl('subsectorAssociationId');
  }

  return group;
}
