import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { textFieldValidators } from '@shared/validators';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { underlyingAgreementQuery } from '../+state/underlying-agreement.selectors';

export type CompanyRegistrationNumberFormModel = {
  isCompanyRegistrationNumber: FormControl<UnderlyingAgreementTargetUnitDetails['isCompanyRegistrationNumber']>;
  companyRegistrationNumber: FormControl<UnderlyingAgreementTargetUnitDetails['companyRegistrationNumber']>;
  registrationNumberMissingReason: FormControl<UnderlyingAgreementTargetUnitDetails['registrationNumberMissingReason']>;
};

export const COMPANY_REGISTRATION_NUMBER_FORM = new InjectionToken<CompanyRegistrationNumberFormModel>(
  'Company Registration Number Form',
);

export const CompanyRegistrationNumberFormProvider: Provider = {
  provide: COMPANY_REGISTRATION_NUMBER_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const tuDetails = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();

    const group = fb.group<CompanyRegistrationNumberFormModel>({
      isCompanyRegistrationNumber: fb.control(tuDetails?.isCompanyRegistrationNumber ?? null, [
        GovukValidators.required('You must select an option'),
      ]),
      companyRegistrationNumber: fb.control(
        tuDetails?.companyRegistrationNumber ?? null,
        textFieldValidators('company number'),
      ),
      registrationNumberMissingReason: fb.control(
        tuDetails?.registrationNumberMissingReason ?? null,
        textFieldValidators('reason for not having company number'),
      ),
    });

    group.controls.isCompanyRegistrationNumber.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((hasRegistrationNumber: boolean) => {
        if (hasRegistrationNumber) {
          group.controls.companyRegistrationNumber.enable();

          group.controls.registrationNumberMissingReason.reset();
          group.controls.registrationNumberMissingReason.disable();
        } else {
          group.controls.registrationNumberMissingReason.enable();

          group.controls.companyRegistrationNumber.reset();
          group.controls.companyRegistrationNumber.disable();
        }
      });

    return group;
  },
};
