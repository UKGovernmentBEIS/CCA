import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { facilityExistenceValidator, underlyingAgreementQuery } from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';
import { facilityIDValidators, textFieldValidators } from '@shared/validators';

import { FacilityDetails, FacilityService } from 'cca-api';

export type VariationFacilityDetailsFormModel = {
  name: FormControl<string>;
  facilityId: FormControl<string>;
  isCoveredByUkets: FormControl<FacilityDetails['isCoveredByUkets']>;
  uketsId: FormControl<FacilityDetails['uketsId']>;
  applicationReason: FormControl<FacilityDetails['applicationReason']>;
  previousFacilityId: FormControl<FacilityDetails['previousFacilityId']>;
  sameAddress: FormControl<boolean[]>;
  facilityAddress: FormGroup<AccountAddressFormModel>;
};

export const VARIATION_FACILITY_DETAILS_FORM = new InjectionToken<VariationFacilityDetailsFormModel>(
  'Variation facility Details Form',
);

export const VariationFacilityDetailsFormProvider: Provider = {
  provide: VARIATION_FACILITY_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, FacilityService],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    facilityService: FacilityService,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();

    const targetUnitAddress = requestTaskStore.select(
      underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails,
    )().address;

    const addressFormGroup = createAccountAddressForm(facility?.facilityDetails?.facilityAddress ?? null);

    const group = fb.group<VariationFacilityDetailsFormModel>({
      name: fb.control(facility?.facilityDetails?.name ?? null, textFieldValidators('site name')),
      facilityId: fb.control({ value: facilityId, disabled: true }),
      isCoveredByUkets: fb.control(facility?.facilityDetails?.isCoveredByUkets ?? null, [
        GovukValidators.required('Select yes if this facility is covered by UK ETS'),
      ]),
      uketsId: fb.control(facility?.facilityDetails?.uketsId ?? null, [
        GovukValidators.required('UK ETS installation identifier cannot be blank'),
        GovukValidators.maxLength(255, `UK ETS installation identifier should not be more than 255 characters`),
      ]),
      applicationReason: fb.control(
        {
          value: facility?.facilityDetails?.applicationReason ?? null,
          disabled: facility && facility?.status !== 'NEW',
        },
        [GovukValidators.required('Select the reason for the application')],
      ),
      previousFacilityId: fb.control(
        {
          value: facility?.facilityDetails?.previousFacilityId ?? null,
          disabled: facility && facility?.status !== 'NEW',
        },
        {
          validators: facilityIDValidators(
            'Enter the facility ID of an existing facility.',
            'The Previous facility ID must be in the same format as the facility number, like AAAA-F00001',
          ),
          asyncValidators: [facilityExistenceValidator(facilityService)],
          updateOn: 'submit',
        },
      ),
      sameAddress: fb.control([false]),
      facilityAddress: addressFormGroup,
    });

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed()).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.facilityAddress.setValue({
          ...targetUnitAddress,
          line2: targetUnitAddress.line2 ?? null,
          county: targetUnitAddress.county ?? null,
        });

        group.controls.facilityAddress.disable();
      } else {
        group.controls.facilityAddress.reset();
        group.controls.facilityAddress.enable();
      }
    });

    return group;
  },
};
