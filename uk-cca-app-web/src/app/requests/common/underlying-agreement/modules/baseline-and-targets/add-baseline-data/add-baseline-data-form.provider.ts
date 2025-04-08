import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { BaselineData } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  TargetPeriod,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '../../../underlying-agreement.types';
import { addBaselineDataConditionallyRequiredFieldsValidator } from '../baseline-and-targets-validators';

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
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, BASELINE_AND_TARGETS_SUBTASK],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
    targetPeriod: TargetPeriod,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();

    const isTargetPeriodFive = targetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

    const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(isTargetPeriodFive))();

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectTargetComposition(isTargetPeriodFive),
    )();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    const greenfieldEvidencesFilesControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      baselineData?.greenfieldEvidences,
      attachments,
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
