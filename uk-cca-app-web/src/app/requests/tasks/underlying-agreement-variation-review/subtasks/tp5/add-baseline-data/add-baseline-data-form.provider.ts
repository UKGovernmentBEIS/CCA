import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  addBaselineDataConditionallyRequiredFieldsValidator,
  normaliseNumber,
  underlyingAgreementQuery,
} from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { BaselineData } from 'cca-api';

export type AddBaselineDataFormModel = FormGroup<{
  energy: FormControl<BaselineData['energy']>;
  isTwelveMonths: FormControl<BaselineData['isTwelveMonths']>;
  baselineDate: FormControl<Date>;
  explanation: FormControl<BaselineData['explanation']>;
  greenfieldEvidences: FormControl<UuidFilePair[]>;
  facility: FormControl<BaselineData['energy']>;
  usedReportingMechanism: FormControl<BaselineData['usedReportingMechanism']>;
  throughput: FormControl<BaselineData['throughput']>;
  energyCarbonFactor: FormControl<BaselineData['energyCarbonFactor']>;
}>;

export const ADD_BASELINE_DATA_FORM = new InjectionToken<AddBaselineDataFormModel>('Add baseline data form');

export const AddBaselineDataFormProvider: Provider = {
  provide: ADD_BASELINE_DATA_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(true))(); // TP5

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectTargetComposition(true), // TP5
    )();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    const greenfieldEvidencesFilesControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      baselineData?.greenfieldEvidences || [],
      attachments,
      'UNDERLYING_AGREEMENT_VARIATION_REVIEW_UPLOAD_ATTACHMENT',
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
        energy: fb.control(normaliseNumber(baselineData?.energy), {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        usedReportingMechanism: fb.control(baselineData?.usedReportingMechanism ?? null),
        throughput: fb.control(normaliseNumber(baselineData?.throughput), {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        energyCarbonFactor: fb.control(normaliseNumber(baselineData?.energyCarbonFactor), {
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
