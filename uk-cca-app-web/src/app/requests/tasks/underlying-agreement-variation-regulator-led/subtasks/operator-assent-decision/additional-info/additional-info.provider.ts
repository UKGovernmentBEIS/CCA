import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementVariationRegulatorLedQuery } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

export const ADDITIONAL_INFO_FORM = new InjectionToken('ADDITIONAL_INFO_FORM');

export type AdditionalInfoFormModel = FormGroup<{
  variationImpactsAgreement: FormControl<boolean>;
  additionalInformation: FormControl<string>;
  files: FormControl<UuidFilePair[]>;
}>;

export function provideAdditionalInfo(): Provider {
  return {
    provide: ADDITIONAL_INFO_FORM,
    deps: [RequestTaskStore, FormBuilder, RequestTaskFileService],
    useFactory: (
      requestTaskStore: RequestTaskStore,
      fb: FormBuilder,
      requestTaskFileService: RequestTaskFileService,
    ) => {
      const determination = requestTaskStore.select(
        underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
      )();

      const attachments = requestTaskStore.select(
        underlyingAgreementVariationRegulatorLedQuery.selectRegulatorLedSubmitAttachments,
      )();

      const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

      const filesControl = requestTaskFileService.buildFormControl(
        requestTaskId,
        determination?.files ?? [],
        attachments,
        'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_UPLOAD_ATTACHMENT',
      );

      const group = fb.group({
        variationImpactsAgreement: fb.control(determination?.variationImpactsAgreement ?? true, [
          GovukValidators.required('Enter a comment'),
        ]),
        additionalInformation: fb.control(determination?.additionalInformation ?? null, [
          GovukValidators.maxLength(10000, 'The additional information should not be more than 10000 characters'),
        ]),
        files: filesControl,
      });

      group.controls.variationImpactsAgreement.valueChanges.pipe(takeUntilDestroyed()).subscribe((impact) => {
        if (impact) {
          group.controls.additionalInformation.reset();
          group.controls.additionalInformation.clearValidators();
        } else {
          group.controls.additionalInformation.reset();
          group.controls.additionalInformation.addValidators(GovukValidators.required('Enter a comment'));
        }

        group.controls.additionalInformation.updateValueAndValidity();
      });

      return group;
    },
  };
}
