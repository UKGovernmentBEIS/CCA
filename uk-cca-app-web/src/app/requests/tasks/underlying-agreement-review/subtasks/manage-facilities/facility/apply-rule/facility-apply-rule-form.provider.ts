import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { normaliseNumber, underlyingAgreementQuery, UPLOAD_SECTION_ATTACHMENT_TYPE } from '@requests/common';
import { FileType, FileValidators, UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { Apply70Rule } from 'cca-api';

export type FacilityApplyRuleFormModel = {
  energyConsumed: FormControl<Apply70Rule['energyConsumed']>;
  energyConsumedProvision: FormControl<Apply70Rule['energyConsumedProvision']>;
  startDate: FormControl<Apply70Rule['startDate']>;
  evidenceFile: FormControl<UuidFilePair>;
};

export const FACILITY_APPLY_RULE_FORM = new InjectionToken<FacilityApplyRuleFormModel>('Facility Apply Rule Form');

export const facilityApplyRuleFormProvider: Provider = {
  provide: FACILITY_APPLY_RULE_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();

    const facilityId = activatedRoute.snapshot.params.facilityId;
    const applyRule = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()?.apply70Rule;
    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();
    const buildFileFormControl = requestTaskFileService.buildFormControl(
      requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
      applyRule?.evidenceFile,
      attachments,
      UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      true,
      false,
    );

    buildFileFormControl.addValidators([
      FileValidators.validContentTypes([FileType.XLSX, FileType.XLS], 'must be an Excel spreadsheet'),
    ]);

    const group = fb.group({
      energyConsumed: fb.control(normaliseNumber(applyRule?.energyConsumed), [
        GovukValidators.required('Enter the energy consumed in the installation'),
        GovukValidators.min(0, 'Percentage of energy consumed in the installation must be between 0 and 100'),
        GovukValidators.max(100, 'Percentage of energy consumed in the installation must be between 0 and 100'),
        GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
      ]),
      energyConsumedProvision: fb.control(normaliseNumber(applyRule?.energyConsumedProvision), [
        GovukValidators.required('Enter the energy consumed in relation to the 3/7ths provision'),
        GovukValidators.min(0, 'Percentage of energy consumed in the installation must be between 0 and 42.9'),
        GovukValidators.max(42.9, 'Percentage of energy consumed in the installation must be between 0 and 42.9'),
        GovukValidators.maxIntegerAndDecimalsValidator(2, 7),
      ]),
      startDate: fb.control(applyRule?.startDate ? new Date(applyRule.startDate) : null),
      evidenceFile: buildFileFormControl,
    });

    group.controls.energyConsumed.valueChanges.pipe(takeUntilDestroyed()).subscribe((energyConsumed) => {
      if (Number(energyConsumed) < 70) {
        group.controls.energyConsumedProvision.enable();
        group.controls.startDate.enable();
      } else {
        group.controls.energyConsumedProvision.disable();
        group.controls.startDate.disable();
        group.controls.energyConsumedProvision.reset();
        group.controls.startDate.reset();
      }
    });

    return group;
  },
};
