import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { FileType, FileValidators, requiredFileValidator } from '@shared/components';

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

export function createForm(fb: FormBuilder, isInviteUserMode: boolean): FormGroup<DetailsForm> {
  return fb.group<DetailsForm>({
    user: fb.group({
      firstName: [
        '',
        [
          GovukValidators.required(`Enter user's first name`),
          GovukValidators.maxLength(255, 'First name should not be more than 255 characters'),
        ],
      ],
      lastName: [
        '',
        [
          GovukValidators.required(`Enter user's last name`),
          GovukValidators.maxLength(255, 'Last name should not be more than 255 characters'),
        ],
      ],
      phoneNumber: [
        '',
        [
          GovukValidators.empty(`Enter user's phone number`),
          GovukValidators.maxLength(255, 'Phone number should not be more than 255 characters'),
        ],
      ],
      mobileNumber: ['', GovukValidators.maxLength(255, 'Mobile number should not be more than 255 characters')],
      email: [
        '',
        [
          GovukValidators.required(`Enter user's email`),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        ],
      ],
      jobTitle: [
        '',
        [
          GovukValidators.required(`Enter user's job title`),
          GovukValidators.maxLength(255, 'Job title should not be more than 255 characters'),
        ],
      ],
    }),
    signature: fb.control(null, {
      validators: [
        FileValidators.validContentTypes([FileType.BMP], 'must be BMP'),
        FileValidators.maxFileSize(0.2, 'must be smaller than 200KB'),
        FileValidators.maxImageDimensionsSize(240, 140, 'must be 240 x 140 pixels'),
        FileValidators.notEmpty(),
        ...(isInviteUserMode ? [requiredFileValidator] : []),
      ],
      updateOn: 'change',
    }),
    permissions: fb.group<PermissionsForm>({
      MANAGE_SECTOR_ASSOCIATIONS: fb.control('NONE'),
      ASSIGN_REASSIGN_TASKS: fb.control('NONE'),
      MANAGE_USERS_AND_CONTACTS: fb.control('NONE'),
      MANAGE_SECTOR_USERS: fb.control('NONE'),
      MANAGE_OPERATOR_USERS: fb.control('NONE'),
      ADMIN_TERMINATION_SUBMISSION: fb.control('NONE'),
      UNDERLYING_AGREEMENT_APPLICATION_REVIEW: fb.control('NONE'),
      UNDERLYING_AGREEMENT_VARIATION_REVIEW: fb.control('NONE'),
    }),
  });
}
