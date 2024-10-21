import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { CcaOperatorUserInvitationDTO } from 'cca-api';

export type AddOperatorForm = FormGroup<{
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  email: FormControl<string>;
  roleCode: FormControl<'operator_basic_user'>;
  contactType: FormControl<CcaOperatorUserInvitationDTO['contactType']>;
}>;

export const ADD_OPERATOR_FORM = new InjectionToken<AddOperatorForm>('ADD_OPERATOR_FORM');

export const AddOperatorFormProvider: Provider = {
  provide: ADD_OPERATOR_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group(
      {
        firstName: fb.control('', { validators: [GovukValidators.required('Enter your first name')] }),
        lastName: fb.control('', {
          validators: [GovukValidators.required('Enter your last name')],
        }),
        email: fb.control('', {
          validators: [
            GovukValidators.required('Enter your email'),
            GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
          ],
        }),
        roleCode: fb.control('operator_basic_user'),
        contactType: fb.control('OPERATOR'),
      },
      { updateOn: 'submit' },
    ),
  deps: [FormBuilder],
};
