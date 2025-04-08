import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementReviewQuery } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

export const ADDITIONAL_INFO_FORM = new InjectionToken('ADDITIONAL_INFO_FORM');

export type AdditionalInfoFormModel = FormGroup<{
  additionalInfo: FormControl<string>;
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
      const determination = requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
      const attachments = requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
      const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

      const filesControl = requestTaskFileService.buildFormControl(
        requestTaskId,
        determination.files ?? [],
        attachments,
        'UNDERLYING_AGREEMENT_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
      );
      return fb.group({
        additionalInfo: fb.control(determination.additionalInformation ?? ''),
        files: filesControl,
      });
    },
  };
}
