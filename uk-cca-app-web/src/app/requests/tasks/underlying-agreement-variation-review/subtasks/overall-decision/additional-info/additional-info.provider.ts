import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery, underlyingAgreementVariationReviewQuery } from '@requests/common';
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
      const determination = requestTaskStore.select(underlyingAgreementVariationReviewQuery.selectDetermination)();

      const filesControl = requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        determination?.files ?? [],
        requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
        'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
      );

      const group = fb.group({
        variationImpactsAgreement: fb.control(determination?.variationImpactsAgreement ?? true),
        additionalInformation: fb.control(determination?.additionalInformation ?? '', [
          GovukValidators.maxLength(10000, 'The additional information should not be more than 10000 characters'),
        ]),
        files: filesControl,
      });

      group.controls.variationImpactsAgreement.valueChanges.pipe(takeUntilDestroyed()).subscribe((impactsAgreement) => {
        if (determination?.type === 'ACCEPTED') {
          if (!impactsAgreement) {
            group.controls.additionalInformation.reset();
            group.controls.additionalInformation.setValidators([
              GovukValidators.required('Enter additional information'),
            ]);
          } else {
            group.controls.additionalInformation.reset();
            group.controls.additionalInformation.clearValidators();
          }

          group.controls.additionalInformation.updateValueAndValidity();
        }
      });

      return group;
    },
  };
}
