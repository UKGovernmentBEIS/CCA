import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  measurementTypeValidator,
  targetCompositionConditionallyRequiredFieldsValidator,
  underlyingAgreementQuery,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '@requests/common';
import { FileType, FileValidators, UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { SchemeVersion } from '@shared/types';

import { TargetComposition } from 'cca-api';

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
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, BASELINE_AND_TARGETS_SUBTASK, DestroyRef],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
    targetPeriod: string,
    destroyRef: DestroyRef,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectTargetComposition(
        targetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
      ),
    )();

    const sectorSchemeData = requestTaskStore.select(
      underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
    )();

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    const calculatorFileControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      targetComposition?.calculatorFile,
      attachments,
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      true,
      !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    );

    calculatorFileControl.addValidators([
      FileValidators.validContentTypes([FileType.XLSX, FileType.XLS], 'must be an Excel spreadsheet'),
    ]);

    const conversionEvidencesFormControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      targetComposition?.conversionEvidences || [],
      attachments,
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    );

    const group = fb.group(
      {
        calculatorFile: calculatorFileControl,
        sectorAssociationMeasurementType: fb.control({
          value: sectorSchemeData?.sectorMeasurementType ?? null,
          disabled: true,
        }),
        sectorAssociationThroughputUnit: fb.control({
          value: sectorSchemeData?.sectorThroughputUnit ?? null,
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

    group.controls.agreementCompositionType.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((act) => {
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

    group.controls.isTargetUnitThroughputMeasured.valueChanges
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe((tutm) => {
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
