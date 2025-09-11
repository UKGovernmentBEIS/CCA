import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { catchError, map, Observable, of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery } from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';
import { facilityIDValidators } from '@shared/validators';

import { FacilityDetails, FacilityItem, FacilityService } from 'cca-api';

export type FacilityDetailsFormModel = {
  facilityId: FormControl<FacilityItem['facilityId']>;
  name: FormControl<string>;
  isCoveredByUkets: FormControl<FacilityDetails['isCoveredByUkets']>;
  uketsId: FormControl<FacilityDetails['uketsId']>;
  applicationReason: FormControl<FacilityDetails['applicationReason']>;
  previousFacilityId: FormControl<FacilityDetails['previousFacilityId']>;
  sameAddress: FormControl<boolean[]>;
  facilityAddress: FormGroup<AccountAddressFormModel>;
};

export const FACILITY_DETAILS_FORM = new InjectionToken<FacilityDetails>('Facility Details Form');

// Facility existence validator function simplified for our purposes
export function facilityExistenceValidator(facilityService: FacilityService) {
  return (group: AbstractControl): Observable<ValidationErrors | null> => {
    const facilityId = group.value;

    if (facilityId) {
      return facilityService.getActiveFacilityParticipatingSchemeVersions(facilityId).pipe(
        map((exists) =>
          exists.length ? null : { facilityIdNotExists: 'Enter the facility ID of an existing facility' },
        ),
        catchError((error) => {
          if (error.status === 404) return of({ facilityIdNotExists: 'Enter the facility ID of an existing facility' });
          return of({ facilityIdError: 'Unable to validate facility ID. Please try again.' });
        }),
      );
    }

    return of(null);
  };
}

export const facilityDetailsFormProvider: Provider = {
  provide: FACILITY_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, FacilityService, DestroyRef],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    facilityService: FacilityService,
    destroyRef: DestroyRef,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;

    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();

    const address = requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails)()
      .address;

    const addressFormGroup = createAccountAddressForm(facility?.facilityDetails?.facilityAddress);

    const group = fb.group<FacilityDetailsFormModel>({
      facilityId: fb.control({
        value: facilityId,
        disabled: true,
      }),
      name: fb.control(facility?.facilityDetails?.name, GovukValidators.required('Select a site name')),
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
          disabled: facility?.status !== 'NEW',
        },
        [GovukValidators.required('Select the reason for the application')],
      ),
      previousFacilityId: fb.control(
        {
          value: facility?.facilityDetails?.previousFacilityId ?? null,
          disabled: facility?.status !== 'NEW',
        },
        facilityIDValidators(
          'Enter the facility ID of an existing facility.',
          'The Previous facility ID must be in the same format as the facility number, like AAAA-F00001',
        ),
        [facilityExistenceValidator(facilityService)],
      ),
      sameAddress: fb.control([false]),
      facilityAddress: addressFormGroup,
    });

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.facilityAddress.setValue({
          ...address,
          line2: address.line2 ?? null,
          county: address.county ?? null,
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
