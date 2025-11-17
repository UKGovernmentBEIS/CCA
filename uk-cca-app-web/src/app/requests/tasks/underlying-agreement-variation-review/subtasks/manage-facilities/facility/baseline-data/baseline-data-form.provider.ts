import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  facilityBaselineDataConditionallyRequiredFieldsValidator,
  FacilityBaselineDataFormModel,
  isCCA3Scheme,
  underlyingAgreementQuery,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '@requests/common';
import { RequestTaskFileService } from '@shared/services';

export const FACILITY_BASELINE_DATA_FORM = new InjectionToken<FacilityBaselineDataFormModel>(
  'Facility baseline data form',
);

export const FacilityBaselineDataFormProvider: Provider = {
  provide: FACILITY_BASELINE_DATA_FORM,
  deps: [ActivatedRoute, FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (
    activatedRoute: ActivatedRoute,
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();
    const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
    const facilityIndex = una.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;

    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();
    const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions ?? [];
    const isCCA3 = facilityId && isCCA3Scheme(schemeVersions);

    const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectFacilityBaselineData(facilityIndex))();

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityTargetComposition(facilityIndex),
    )();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    const greenfieldEvidencesFilesControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      baselineData?.greenfieldEvidences || [],
      attachments || {},
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    );

    return fb.group(
      {
        isTwelveMonths: fb.control(baselineData?.isTwelveMonths ?? null, {
          validators: [
            GovukValidators.required('Select yes if at least 12 months of consecutive baseline data is available'),
          ],
          updateOn: 'change',
        }),
        baselineDate: fb.control(baselineData?.baselineDate ? new Date(baselineData.baselineDate) : null, {
          updateOn: 'change',
        }),
        explanation: fb.control(baselineData?.explanation ?? null),
        greenfieldEvidences: greenfieldEvidencesFilesControl,
        usedReportingMechanism: fb.control(baselineData?.usedReportingMechanism ?? null, { updateOn: 'change' }),
        energyCarbonFactor: fb.control(baselineData?.energyCarbonFactor ?? null, {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
      },
      {
        validators: facilityBaselineDataConditionallyRequiredFieldsValidator(
          targetComposition?.agreementCompositionType,
          isCCA3,
        ),
        updateOn: 'submit',
      },
    );
  },
};
