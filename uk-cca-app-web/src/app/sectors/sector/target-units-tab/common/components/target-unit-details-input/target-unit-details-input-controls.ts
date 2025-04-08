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
  isEditable?: boolean,
): FormGroup<TargetUnitCreationFormModel> {
  const group = fb.group<TargetUnitCreationFormModel>(
    {
      operatorType: fb.control(
        {
          value: targetUnitAccountDetails?.operatorType,
          disabled: isEditable,
        },
        [GovukValidators.required('You must select an operator type')],
      ),
      name: fb.control(
        {
          value: targetUnitAccountDetails?.name,
          disabled: isEditable,
        },
        textFieldValidators('operator name'),
      ),
      isCompanyRegistrationNumber: fb.control(
        {
          value: isEditable
            ? !!targetUnitAccountDetails?.companyRegistrationNumber
            : targetUnitAccountDetails?.isCompanyRegistrationNumber !== false,
          disabled: isEditable,
        },
        [GovukValidators.required('You must select an option')],
      ),
      companyRegistrationNumber: fb.control(
        {
          value: targetUnitAccountDetails?.companyRegistrationNumber,
          disabled: isEditable,
        },
        textFieldValidators('registration number'),
      ),
      registrationNumberMissingReason: fb.control(
        {
          value: targetUnitAccountDetails?.registrationNumberMissingReason,
          disabled: isEditable,
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
          disabled: isEditable,
        },
        subSectors.length ? [GovukValidators.required('You must select a subsector')] : null,
      ),
    );
  } else {
    group.removeControl('subsectorAssociationId');
  }

  return group;
}
