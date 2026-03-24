import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { FileType, FileValidators, requiredFileValidator, UuidFilePair } from '@shared/components';

import { RegulatorCurrentUserDTO, RegulatorUserDTO } from 'cca-api';

type UserForm = {
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  phoneNumber: FormControl<string>;
  mobileNumber: FormControl<string>;
  email: FormControl<string>;
  jobTitle: FormControl<string>;
};

type PermissionsForm = Record<string, FormControl<'NONE' | 'EXECUTE' | 'VIEW_ONLY'>>;

type DetailsForm = {
  user: FormGroup<UserForm>;
  signature: FormControl<{ uuid: string; file: File }>;
  permissions: FormGroup<PermissionsForm>;
};

export function createForm(
  fb: FormBuilder,
  isInviteUserMode: boolean,
  user: RegulatorUserDTO | RegulatorCurrentUserDTO,
  userPermissions: Record<string, 'NONE' | 'EXECUTE' | 'VIEW_ONLY'>,
): FormGroup<DetailsForm> {
  const userGroup = fb.group<UserForm>({
    email: fb.control({ value: user?.email || '', disabled: !!user }, [
      GovukValidators.required(`Enter user's email`),
      GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
    ]),
    firstName: fb.control(user?.firstName || '', [
      GovukValidators.required(`Enter user's first name`),
      GovukValidators.maxLength(255, 'First name should not be more than 255 characters'),
    ]),
    lastName: fb.control(user?.lastName || '', [
      GovukValidators.required(`Enter user's last name`),
      GovukValidators.maxLength(255, 'Last name should not be more than 255 characters'),
    ]),
    phoneNumber: fb.control(user?.phoneNumber || '', [
      GovukValidators.empty(`Enter user's phone number`),
      GovukValidators.maxLength(255, 'Phone number should not be more than 255 characters'),
    ]),
    mobileNumber: fb.control(user?.mobileNumber || '', [
      GovukValidators.maxLength(255, 'Mobile number should not be more than 255 characters'),
    ]),
    jobTitle: fb.control(user?.jobTitle || '', [
      GovukValidators.required(`Enter user's job title`),
      GovukValidators.maxLength(255, 'Job title should not be more than 255 characters'),
    ]),
  });

  const permissionsGroup = fb.group<PermissionsForm>({
    MANAGE_SECTOR_ASSOCIATIONS: fb.control(userPermissions?.MANAGE_SECTOR_ASSOCIATIONS || 'NONE'),
    ASSIGN_REASSIGN_TASKS: fb.control(userPermissions?.ASSIGN_REASSIGN_TASKS || 'NONE'),
    MANAGE_USERS_AND_CONTACTS: fb.control(userPermissions?.MANAGE_USERS_AND_CONTACTS || 'NONE'),
    MANAGE_SECTOR_USERS: fb.control(userPermissions?.MANAGE_SECTOR_USERS || 'NONE'),
    MANAGE_OPERATOR_USERS: fb.control(userPermissions?.MANAGE_OPERATOR_USERS || 'NONE'),
    ADMIN_TERMINATION_SUBMISSION: fb.control(userPermissions?.ADMIN_TERMINATION_SUBMISSION || 'NONE'),
    ADMIN_TERMINATION_PEER_REVIEW: fb.control(userPermissions?.ADMIN_TERMINATION_PEER_REVIEW || 'NONE'),
    UNDERLYING_AGREEMENT_APPLICATION_REVIEW: fb.control(
      userPermissions?.UNDERLYING_AGREEMENT_APPLICATION_REVIEW || 'NONE',
    ),
    UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW: fb.control(
      userPermissions?.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW || 'NONE',
    ),
    UNDERLYING_AGREEMENT_VARIATION_REVIEW: fb.control(userPermissions?.UNDERLYING_AGREEMENT_VARIATION_REVIEW || 'NONE'),
    UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW: fb.control(
      userPermissions?.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW || 'NONE',
    ),
    UNDERLYING_AGREEMENT_VARIATION_SUBMISSION: fb.control(
      userPermissions?.UNDERLYING_AGREEMENT_VARIATION_SUBMISSION || 'NONE',
    ),
    UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW: fb.control(
      userPermissions?.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW || 'NONE',
    ),
    MANAGE_FACILITY_AUDIT: fb.control(userPermissions?.MANAGE_FACILITY_AUDIT || 'NONE'),
    FACILITY_AUDIT_SUBMISSION: fb.control(userPermissions?.FACILITY_AUDIT_SUBMISSION || 'NONE'),
    NON_COMPLIANCE_SUBMISSION: fb.control(userPermissions?.NON_COMPLIANCE_SUBMISSION || 'NONE'),
  });

  return fb.group<DetailsForm>({
    user: userGroup,
    permissions: permissionsGroup,
    signature: fb.control(
      user?.signature?.uuid
        ? ({
            uuid: user?.signature.uuid,
            file: { name: user?.signature.name } as File,
          } as UuidFilePair)
        : null,
      {
        validators: [
          FileValidators.validContentTypes([FileType.BMP], 'must be BMP'),
          FileValidators.maxFileSize(0.2, 'must be smaller than 200KB'),
          FileValidators.maxImageDimensionsSize(240, 140, 'must be 240 x 140 pixels'),
          FileValidators.notEmpty(),
          ...(isInviteUserMode ? [requiredFileValidator] : []),
        ],
        updateOn: 'change',
      },
    ),
  });
}
