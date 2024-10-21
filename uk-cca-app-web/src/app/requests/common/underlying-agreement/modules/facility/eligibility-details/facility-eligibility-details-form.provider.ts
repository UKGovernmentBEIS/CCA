import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { transformAttachmentToFileWithUUID, transformFilesToUUIDsList } from '@shared/utils';
import { facilityIDValidators } from '@shared/validators';

import { EligibilityDetailsAndAuthorisation } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import { UPLOAD_SECTION_ATTACHMENT_TYPE } from '../../../underlying-agreement.types';

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

    const eligibilityDetails = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()
      ?.eligibilityDetailsAndAuthorisation;

    const facility = requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === facilityId);

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();
    const file = transformAttachmentToFileWithUUID(eligibilityDetails?.permitFile, attachments);

    return fb.group({
      name: fb.control({
        value: facility.name,
        disabled: true,
      }),
      isConnectedToExistingFacility: fb.control(eligibilityDetails?.isConnectedToExistingFacility ?? null, [
        GovukValidators.required('Select yes if the facility is adjacent to or connected to an existing CCA facility'),
      ]),
      adjacentFacilityId: fb.control(
        eligibilityDetails?.adjacentFacilityId ?? null,
        facilityIDValidators(
          'Facility ID of adjacent facility cannot be blank.',
          'The adjacent facility ID must be in the same format as the facility number, like AAAA-F00001',
        ),
      ),
      agreementType: fb.control(eligibilityDetails?.agreementType ?? null, [
        GovukValidators.required('Select the agreement type from the list'),
      ]),
      erpAuthorisationExists: fb.control(eligibilityDetails?.erpAuthorisationExists ?? null, [
        GovukValidators.required(
          'Select yes if you hold a current Environmental Permitting Regulations (EPR) authorisation for any activity being carried out in the facility',
        ),
      ]),
      authorisationNumber: fb.control(eligibilityDetails?.authorisationNumber ?? null, [
        GovukValidators.required('The authorisation number cannot be blank.'),
        GovukValidators.maxLength(255, `The authorisation number should not be more than 255 characters`),
      ]),
      regulatorName: fb.control(eligibilityDetails?.regulatorName ?? null, [
        GovukValidators.required('Select the regulator name'),
      ]),
      permitFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(file),
        attachments,
        UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
        true,
        false,
      ),
    });
  },
};
