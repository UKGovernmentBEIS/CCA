import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  FacilityTargetCompositionFormModel,
  measurementTypeValidator,
  underlyingAgreementQuery,
  UPLOAD_SECTION_ATTACHMENT_TYPE,
} from '@requests/common';
import { FileType, FileValidators } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { SchemeVersion } from '@shared/types';

export const TARGET_COMPOSITION_FORM = new InjectionToken<FacilityTargetCompositionFormModel>(
  'Facility target composition form',
);

export const TargetCompositionFormProvider: Provider = {
  provide: TARGET_COMPOSITION_FORM,
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

    const targetComposition = requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityTargetComposition(facilityIndex),
    )();

    const participatingSchemeVersions = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()
      ?.facilityDetails?.participatingSchemeVersions;

    const sectorSchemeData = requestTaskStore.select(
      underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(
        participatingSchemeVersions.some((s) => s === 'CCA_3') ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
      ),
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
        agreementCompositionType: fb.control(targetComposition?.agreementCompositionType ?? null),
      },
      {
        updateOn: 'submit',
      },
    );

    return group;
  },
};
