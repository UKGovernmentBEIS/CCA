import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  addBaselineDataConditionallyRequiredFieldsValidator,
  AddBaselineDataFormModel,
  underlyingAgreementQuery,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '@requests/common';
import { RequestTaskFileService } from '@shared/services';

export const ADD_BASELINE_DATA_FORM = new InjectionToken<AddBaselineDataFormModel>('Add baseline data form');

export const addBaselineDataFormProvider: Provider = {
  provide: ADD_BASELINE_DATA_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(false))();

    const targetComposition = requestTaskStore.select(underlyingAgreementQuery.selectTargetComposition(false))();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    const greenfieldEvidencesFilesControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      baselineData?.greenfieldEvidences || [],
      attachments || {},
      UPLOAD_SECTION_ATTACHMENT_TYPE.UNDERLYING_AGREEMENT_VARIATION_SUBMIT,
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
        energy: fb.control(baselineData?.energy ?? null, {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        usedReportingMechanism: fb.control(baselineData?.usedReportingMechanism ?? null),
        throughput: fb.control(baselineData?.throughput ?? null, {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        energyCarbonFactor: fb.control(baselineData?.energyCarbonFactor ?? null, {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
      },
      {
        validators: addBaselineDataConditionallyRequiredFieldsValidator(targetComposition?.agreementCompositionType),
        updateOn: 'submit',
      },
    );
  },
};
