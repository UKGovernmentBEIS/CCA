import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery } from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm, phoneInputValidators } from '@shared/components';
import { textFieldValidators } from '@shared/validators';

import { TargetUnitAccountContactDTO } from 'cca-api';

export type FacilityContactFormModel = {
  sameContact: FormControl<boolean[]>;
  firstName: FormControl<TargetUnitAccountContactDTO['firstName']>;
  lastName: FormControl<TargetUnitAccountContactDTO['lastName']>;
  email: FormControl<TargetUnitAccountContactDTO['email']>;
  sameAddress: FormControl<boolean[]>;
  address: FormGroup<AccountAddressFormModel>;
  phoneNumber: FormControl<TargetUnitAccountContactDTO['phoneNumber']>;
};

export const FACILITY_CONTACT_DETAILS_FORM = new InjectionToken<TargetUnitAccountContactDTO>(
  'Facility Contact Details Form',
);

export const facilityContactDetailsFormProvider: Provider = {
  provide: FACILITY_CONTACT_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, DestroyRef],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    destroyRef: DestroyRef,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;

    const facilityContact = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()
      ?.facilityContact;

    const administrative = requestTaskStore.select(
      underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails,
    )()?.administrativeContactDetails;

    const facilityAddress = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()
      .facilityDetails.facilityAddress;

    const addressFormGroup = createAccountAddressForm(facilityContact?.address);

    const group = fb.group<FacilityContactFormModel>({
      sameContact: fb.control([false]),
      firstName: fb.control(facilityContact?.firstName ?? null, textFieldValidators('first name')),
      lastName: fb.control(facilityContact?.lastName ?? null, textFieldValidators('last name')),
      email: fb.control(facilityContact?.email ?? null, [
        ...textFieldValidators('email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
      ]),
      sameAddress: fb.control([false]),
      address: addressFormGroup,
      phoneNumber: fb.control(facilityContact?.phoneNumber ?? null, phoneInputValidators),
    });

    group.controls.sameContact.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameContact) => {
      if (isSameContact[0]) {
        group.controls.firstName.setValue(administrative.firstName);
        group.controls.lastName.setValue(administrative.lastName);
        group.controls.email.setValue(administrative.email);

        group.controls.firstName.disable();
        group.controls.lastName.disable();
        group.controls.email.disable();
      } else {
        group.controls.firstName.reset();
        group.controls.lastName.reset();
        group.controls.email.reset();

        group.controls.firstName.enable();
        group.controls.lastName.enable();
        group.controls.email.enable();
      }
    });

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.address.setValue({
          ...facilityAddress,
          line2: facilityAddress.line2 ?? null,
          county: facilityAddress.county ?? null,
        });

        group.controls.address.disable();
      } else {
        group.controls.address.reset();
        group.controls.address.enable();
      }
    });

    return group;
  },
};
