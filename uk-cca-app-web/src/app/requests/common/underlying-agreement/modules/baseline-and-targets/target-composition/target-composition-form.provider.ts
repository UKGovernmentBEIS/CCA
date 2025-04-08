import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { FileType, FileValidators, UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import {
  transformAttachmentsToFilesWithUUIDs,
  transformAttachmentToFileWithUUID,
  transformFilesToUUIDsList,
} from '@shared/utils';

import { TargetComposition } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  TargetPeriod,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '../../../underlying-agreement.types';
import {
  measurementTypeValidator,
  targetCompositionConditionallyRequiredFieldsValidator,
} from '../baseline-and-targets-validators';

export type TargetCompositionFormModel = FormGroup<{
  calculatorFile: FormControl<UuidFilePair>;
  sectorAssociationMeasurementType: FormControl<string>;
  sectorAssociationThroughputUnit: FormControl<string>;
  measurementType: FormControl<TargetComposition['measurementType'] | null>;
  agreementCompositionType: FormControl<TargetComposition['agreementCompositionType']>;
  isTargetUnitThroughputMeasured: FormControl<boolean>;
  throughputUnit: FormControl<string>;
  conversionFactor: FormControl<number>;
  conversionEvidences: FormControl<UuidFilePair[]>;
}>;

export const TARGET_COMPOSITION_FORM = new InjectionToken<TargetCompositionFormModel>('Target composition form');

export const TargetCompositionFormProvider: Provider = {
  provide: TARGET_COMPOSITION_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, BASELINE_AND_TARGETS_SUBTASK],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
    targetPeriod: TargetPeriod,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectTargetComposition(
        targetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
      ),
    )();

    const accountReferenceData = requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)();
    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();
    const calculatorFile = transformAttachmentToFileWithUUID(targetComposition?.calculatorFile, attachments);
    const calculatorFileControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      transformFilesToUUIDsList(calculatorFile),
      attachments,
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      true,
      !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    );

    calculatorFileControl.addValidators([
      FileValidators.validContentTypes([FileType.XLSX, FileType.XLS], 'must be an Excel spreadsheet'),
    ]);

    const conversionEvidencesFiles = transformAttachmentsToFilesWithUUIDs(
      targetComposition?.conversionEvidences,
      attachments,
    );

    const conversionEvidencesFormControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      transformFilesToUUIDsList(conversionEvidencesFiles),
      attachments,
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    );

    const group = fb.group(
      {
        calculatorFile: calculatorFileControl,
        sectorAssociationMeasurementType: fb.control({
          value: accountReferenceData?.sectorAssociationDetails?.measurementType ?? null,
          disabled: true,
        }),
        sectorAssociationThroughputUnit: fb.control({
          value: accountReferenceData?.sectorAssociationDetails?.throughputUnit ?? null,
          disabled: true,
        }),
        measurementType: fb.control(targetComposition?.measurementType ?? null, [measurementTypeValidator()]),
        agreementCompositionType: fb.control(targetComposition?.agreementCompositionType ?? null, {
          validators: [GovukValidators.required('Select the target type')],
          updateOn: 'change',
        }),
        isTargetUnitThroughputMeasured: fb.control(targetComposition?.isTargetUnitThroughputMeasured ?? null, {
          updateOn: 'change',
        }),
        throughputUnit: fb.control(targetComposition?.throughputUnit),
        conversionFactor: fb.control(targetComposition?.conversionFactor ?? null, {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        conversionEvidences: conversionEvidencesFormControl,
      },
      {
        validators: [targetCompositionConditionallyRequiredFieldsValidator()],
        updateOn: 'submit',
      },
    );

    group.controls.agreementCompositionType.valueChanges.pipe(takeUntilDestroyed()).subscribe((act) => {
      if (act === 'NOVEM') {
        group.controls.isTargetUnitThroughputMeasured.disable();
        group.controls.throughputUnit.disable();
        group.controls.conversionFactor.disable();
        group.controls.conversionEvidences.disable();

        group.controls.isTargetUnitThroughputMeasured.reset();
        group.controls.throughputUnit.reset();
        group.controls.conversionFactor.reset();
        group.controls.conversionEvidences.reset();
      } else {
        group.controls.isTargetUnitThroughputMeasured.enable();
        group.controls.throughputUnit.enable();
        group.controls.conversionFactor.enable();
        group.controls.conversionEvidences.enable();
      }
    });

    group.controls.isTargetUnitThroughputMeasured.valueChanges.pipe(takeUntilDestroyed()).subscribe((tutm) => {
      if (typeof tutm !== 'boolean') return;

      if (!tutm) {
        group.controls.conversionFactor.disable();
        group.controls.conversionEvidences.disable();

        group.controls.conversionFactor.reset();
        group.controls.conversionEvidences.reset();
        group.controls.throughputUnit.reset();
      } else {
        group.controls.conversionFactor.enable();
        group.controls.conversionEvidences.enable();
      }
    });

    return group;
  },
};
