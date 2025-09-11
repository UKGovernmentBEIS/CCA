import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

export type AddSectorFormModel = FormGroup<{
  email: FormControl<string>;
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  roleCode?: FormControl<string>;
  contactType?: FormControl<'SECTOR_ASSOCIATION' | 'CONSULTANT'>;
}>;

export const ADD_SECTOR_FORM = new InjectionToken<AddSectorFormModel>('Add sector Form');

export const AddSectorFormProvider: Provider = {
  provide: ADD_SECTOR_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const role = route.snapshot.queryParamMap.get('role');

    return fb.group(
      {
        firstName: fb.control('', GovukValidators.required('Enter the user’s first name')),
        lastName: fb.control('', GovukValidators.required('Enter the user’s last name')),
        email: fb.control('', [
          GovukValidators.required('Enter the user’s email'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        ]),
        roleCode: fb.control(role, [GovukValidators.required('Enter a valid role code')]),
        contactType: fb.control('SECTOR_ASSOCIATION', GovukValidators.required('Select a contact type')),
      },
      {
        updateOn: 'submit',
      },
    );
  },
};
