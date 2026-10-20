import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { catchError, debounceTime, filter, map, Observable, of, switchMap, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  facilityExistenceValidator,
  hasBothCCASchemes,
  isCCA2Scheme,
  isCCA3Scheme,
  isCreationDateAfterCutOffDate,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressFormModel, createAccountAddressForm } from '@shared/components';
import { ConfigService } from '@shared/config';
import { UK_COUNTRY_CODES } from '@shared/services';
import { SchemeVersion, SchemeVersions } from '@shared/types';
import { facilityIDValidators, textFieldValidators } from '@shared/validators';

import { FacilityDetails, FacilityService } from 'cca-api';

export type VariationFacilityDetailsFormModel = {
  name: FormControl<string>;
  facilityId: FormControl<string>;
  isCoveredByUkets: FormControl<FacilityDetails['isCoveredByUkets']>;
  uketsId: FormControl<FacilityDetails['uketsId']>;
  applicationReason: FormControl<FacilityDetails['applicationReason']>;
  previousFacilityId: FormControl<FacilityDetails['previousFacilityId']>;
  participatingSchemeVersions: FormControl<SchemeVersions>;
  schemeParticipationChoice: FormControl<boolean | null>;
  sameAddress?: FormControl<boolean[]>;
  facilityAddress: FormGroup<AccountAddressFormModel>;
};

export const VARIATION_FACILITY_DETAILS_FORM = new InjectionToken<VariationFacilityDetailsFormModel>(
  'Variation facility Details Form',
);

export const VariationFacilityDetailsFormProvider: Provider = {
  provide: VARIATION_FACILITY_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, FacilityService, ConfigService],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    facilityService: FacilityService,
    configService: ConfigService,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();

    const tuOperatorAddress = requestTaskStore.select(
      underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
    )()?.operatorAddress;

    const isTUOperatorAddressUKCountry = UK_COUNTRY_CODES.includes(tuOperatorAddress?.country);

    const isAfterCutOffDate = isCreationDateAfterCutOffDate(
      requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.creationDate,
      configService.getUnderlyingAgreementSchemeParticipationFlagCutOffDate(),
    );

    const previousFacilityId = facility?.facilityDetails?.previousFacilityId;
    const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions;

    let schemeParticipationChoice: boolean | null = null;

    if (previousFacilityId || facility?.status === 'LIVE') {
      if (hasBothCCASchemes(schemeVersions)) {
        schemeParticipationChoice = true;
      } else if (isCCA2Scheme(schemeVersions)) {
        schemeParticipationChoice = false;
      }
    }

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
      previousFacilityId: fb.control({
        value: facility?.facilityDetails?.previousFacilityId ?? null,
        disabled: facility && facility?.status !== 'NEW',
      }),
      participatingSchemeVersions: fb.control(facility?.facilityDetails?.participatingSchemeVersions ?? []),
      schemeParticipationChoice: fb.control<boolean | null>(schemeParticipationChoice),
      facilityAddress: addressFormGroup,
    });

    handleSchemeParticipationLogic(group, facility?.facilityDetails?.participatingSchemeVersions, isAfterCutOffDate);

    if (
      group.controls.applicationReason.value === 'CHANGE_OF_OWNERSHIP' &&
      !group.controls.previousFacilityId.disabled
    ) {
      setupChangeOfOwnershipValidators(group, facilityService);
    }

    group.controls.applicationReason.valueChanges.pipe(takeUntilDestroyed()).subscribe((reason) => {
      handleApplicationReasonChange(reason, group, facilityService);
    });

    group.controls.previousFacilityId.valueChanges
      .pipe(
        takeUntilDestroyed(),
        filter(
          () =>
            group.controls.applicationReason.value === 'CHANGE_OF_OWNERSHIP' &&
            !group.controls.previousFacilityId.disabled,
        ),
        debounceTime(300),
        switchMap((previousFacilityId) => {
          if (previousFacilityId?.trim()) {
            return getFacilitySchemeParticipation(facilityService, previousFacilityId).pipe(
              // Only emit when we have actual data (success or error)
              filter((result) => result !== undefined),
            );
          }

          return of([]);
        }),
        tap((schemeVersions) => {
          handleSchemeParticipationLogic(group, schemeVersions, isAfterCutOffDate);
        }),
      )
      .subscribe();

    if (!isAfterCutOffDate) {
      group.controls.schemeParticipationChoice.valueChanges.pipe(takeUntilDestroyed()).subscribe((choice) => {
        updateSchemeParticipation(group, choice);
      });
    }

    if (isTUOperatorAddressUKCountry) {
      group.addControl('sameAddress', fb.control([false]));

      group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed()).subscribe((isSameAddress) => {
        if (isSameAddress[0]) {
          group.controls.facilityAddress.setValue({
            ...tuOperatorAddress,
            line2: tuOperatorAddress.line2 ?? null,
            county: tuOperatorAddress.county ?? null,
          });

          group.controls.facilityAddress.disable();
        } else {
          group.controls.facilityAddress.reset();
          group.controls.facilityAddress.enable();
        }
      });
    }

    return group;
  },
};

function getFacilitySchemeParticipation(
  facilityService: FacilityService,
  facilityId: string,
): Observable<SchemeVersions> {
  return facilityService.getActiveFacilityParticipatingSchemeVersions(facilityId).pipe(
    map((schemeVersions) => (schemeVersions as SchemeVersions) || []),
    catchError(() => of([])),
  );
}

function setupChangeOfOwnershipValidators(
  group: FormGroup<VariationFacilityDetailsFormModel>,
  facilityService: FacilityService,
) {
  const previousFacilityCtrl = group.controls.previousFacilityId;

  previousFacilityCtrl.enable();
  previousFacilityCtrl.setValidators(
    facilityIDValidators(
      'Enter the facility ID of an existing facility.',
      'The Previous facility ID must be in the same format as the facility number, like AAAA-F00001',
    ),
  );
  previousFacilityCtrl.setAsyncValidators(facilityExistenceValidator(facilityService));
  previousFacilityCtrl.updateValueAndValidity();
}

function handleApplicationReasonChange(
  reason: 'NEW_AGREEMENT' | 'CHANGE_OF_OWNERSHIP' | null,
  group: FormGroup<VariationFacilityDetailsFormModel>,
  facilityService: FacilityService,
) {
  const previousFacilityIdCtrl = group.controls.previousFacilityId;
  const schemeParticipationChoiceCtrl = group.controls.schemeParticipationChoice;
  const participatingSchemeVersionsCtrl = group.controls.participatingSchemeVersions;

  if (reason === 'NEW_AGREEMENT') {
    previousFacilityIdCtrl.disable({ emitEvent: false });
    previousFacilityIdCtrl.reset();

    schemeParticipationChoiceCtrl.disable({ emitEvent: false });
    schemeParticipationChoiceCtrl.reset();

    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_3], { emitEvent: false });

    previousFacilityIdCtrl.updateValueAndValidity({ emitEvent: true });
    schemeParticipationChoiceCtrl.updateValueAndValidity({ emitEvent: true });
    participatingSchemeVersionsCtrl.updateValueAndValidity({ emitEvent: true });
  } else {
    setupChangeOfOwnershipValidators(group, facilityService);

    participatingSchemeVersionsCtrl.setValue([], { emitEvent: false });
    schemeParticipationChoiceCtrl.reset();
    schemeParticipationChoiceCtrl.disable({ emitEvent: false });

    schemeParticipationChoiceCtrl.updateValueAndValidity({ emitEvent: true });
    participatingSchemeVersionsCtrl.updateValueAndValidity({ emitEvent: true });
  }
}

function handleSchemeParticipationLogic(
  group: FormGroup<VariationFacilityDetailsFormModel>,
  previousFacilitySchemes: SchemeVersions | null,
  isAfterCutOffDate: boolean,
) {
  const schemeParticipationChoiceCtrl = group.controls.schemeParticipationChoice;
  const participatingSchemeVersionsCtrl = group.controls.participatingSchemeVersions;

  if (!previousFacilitySchemes || previousFacilitySchemes.length === 0) {
    // No data available - reset everything (batch updates)
    schemeParticipationChoiceCtrl.disable({ emitEvent: false });
    schemeParticipationChoiceCtrl.reset();
    participatingSchemeVersionsCtrl.setValue([], { emitEvent: false });

    // Emit once at the end
    schemeParticipationChoiceCtrl.updateValueAndValidity({ emitEvent: true });
    participatingSchemeVersionsCtrl.updateValueAndValidity({ emitEvent: true });
    return;
  }

  if (isAfterCutOffDate) {
    schemeParticipationChoiceCtrl.disable({ emitEvent: false });
    schemeParticipationChoiceCtrl.reset();

    if (hasBothCCASchemes(previousFacilitySchemes)) {
      participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2, SchemeVersion.CCA_3], { emitEvent: false });
    } else if (isCCA3Scheme(previousFacilitySchemes)) {
      participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_3], { emitEvent: false });
    } else {
      participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2], { emitEvent: false });
    }

    // Emit once at the end
    schemeParticipationChoiceCtrl.updateValueAndValidity({ emitEvent: true });
    participatingSchemeVersionsCtrl.updateValueAndValidity({ emitEvent: true });
    return;
  }

  if (hasBothCCASchemes(previousFacilitySchemes)) {
    // Previous facility has both -> show user choice
    schemeParticipationChoiceCtrl.enable({ emitEvent: false });
    schemeParticipationChoiceCtrl.setValidators([
      GovukValidators.required('Select yes if this facility will participate in CCA3'),
    ]);
    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2, SchemeVersion.CCA_3], { emitEvent: false });
  } else if (isCCA3Scheme(previousFacilitySchemes)) {
    // Previous facility has CCA3 -> automatically set to CCA3
    schemeParticipationChoiceCtrl.disable({ emitEvent: false });
    schemeParticipationChoiceCtrl.reset();
    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_3], { emitEvent: false });
  } else {
    // Previous facility has CCA2 -> show user choice
    schemeParticipationChoiceCtrl.enable({ emitEvent: false });
    schemeParticipationChoiceCtrl.setValidators([
      GovukValidators.required('Select yes if this facility will participate in CCA3'),
    ]);
    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2], { emitEvent: false });
  }

  // Emit once at the end
  schemeParticipationChoiceCtrl.updateValueAndValidity({ emitEvent: true });
  participatingSchemeVersionsCtrl.updateValueAndValidity({ emitEvent: true });
}

function updateSchemeParticipation(group: FormGroup<VariationFacilityDetailsFormModel>, userChoice: boolean | null) {
  const participatingSchemeVersionsCtrl = group.controls.participatingSchemeVersions;

  if (userChoice === true) {
    // User chose Yes -> BOTH CCA2 and CCA3
    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2, SchemeVersion.CCA_3]);
  } else if (userChoice === false) {
    // User chose No -> CCA2 only
    participatingSchemeVersionsCtrl.setValue([SchemeVersion.CCA_2]);
  }
}
