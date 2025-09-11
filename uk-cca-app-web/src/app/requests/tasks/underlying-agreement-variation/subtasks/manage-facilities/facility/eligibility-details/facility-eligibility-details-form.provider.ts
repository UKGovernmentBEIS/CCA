import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery, UPLOAD_SECTION_ATTACHMENT_TYPE } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { facilityIDValidators } from '@shared/validators';

import { EligibilityDetailsAndAuthorisation } from 'cca-api';

export type FacilityEligibilityFormModel = {
  name: FormControl<string>;
  isConnectedToExistingFacility: FormControl<EligibilityDetailsAndAuthorisation['isConnectedToExistingFacility']>;
  adjacentFacilityId: FormControl<EligibilityDetailsAndAuthorisation['adjacentFacilityId']>;
  agreementType: FormControl<EligibilityDetailsAndAuthorisation['agreementType']>;
  erpAuthorisationExists: FormControl<EligibilityDetailsAndAuthorisation['erpAuthorisationExists']>;
  authorisationNumber: FormControl<EligibilityDetailsAndAuthorisation['authorisationNumber']>;
  regulatorName: FormControl<EligibilityDetailsAndAuthorisation['regulatorName']>;
  permitFile: FormControl<UuidFilePair>;
};

export const FACILITY_ELIGIBILITY_DETAILS_FORM = new InjectionToken<EligibilityDetailsAndAuthorisation>(
  'Facility Eligibility Details Form',
);

export const FacilityEligibilityDetailsFormProvider: Provider = {
  provide: FACILITY_ELIGIBILITY_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();
    const facilityId = activatedRoute.snapshot.params.facilityId;

    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();
    return fb.group({
      name: fb.control({
        value: facility?.facilityDetails?.name,
        disabled: true,
      }),
      isConnectedToExistingFacility: fb.control(
        facility.eligibilityDetailsAndAuthorisation?.isConnectedToExistingFacility ?? null,
        [
          GovukValidators.required(
            'Select yes if the facility is adjacent to or connected to an existing CCA facility',
          ),
        ],
      ),
      adjacentFacilityId: fb.control(
        facility.eligibilityDetailsAndAuthorisation?.adjacentFacilityId ?? null,
        facilityIDValidators(
          'Facility ID of adjacent facility cannot be blank.',
          'The adjacent facility ID must be in the same format as the facility number, like AAAA-F00001',
        ),
      ),
      agreementType: fb.control(facility.eligibilityDetailsAndAuthorisation?.agreementType ?? null, [
        GovukValidators.required('Select the agreement type from the list'),
      ]),
      erpAuthorisationExists: fb.control(facility.eligibilityDetailsAndAuthorisation?.erpAuthorisationExists ?? null, [
        GovukValidators.required(
          'Select yes if you hold a current Environmental Permitting Regulations (EPR) authorisation for any activity being carried out in the facility',
        ),
      ]),
      authorisationNumber: fb.control(facility.eligibilityDetailsAndAuthorisation?.authorisationNumber ?? null, [
        GovukValidators.required('The authorisation number cannot be blank.'),
        GovukValidators.maxLength(255, `The authorisation number should not be more than 255 characters`),
      ]),
      regulatorName: fb.control(facility.eligibilityDetailsAndAuthorisation?.regulatorName ?? null, [
        GovukValidators.required('Select the regulator name'),
      ]),
      permitFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facility.eligibilityDetailsAndAuthorisation?.permitFile,
        attachments,
        UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
        true,
        false,
      ),
    });
  },
};
