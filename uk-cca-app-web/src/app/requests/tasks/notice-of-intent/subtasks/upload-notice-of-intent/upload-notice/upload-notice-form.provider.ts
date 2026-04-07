import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { NoticeOfIntent } from 'cca-api';

import { noticeOfIntentQuery } from '../../../notice-of-intent.selectors';

export type UploadNoticeOfIntentFormModel = FormGroup<{
  noticeOfIntentFile: FormControl<UuidFilePair>;
  comments: FormControl<NoticeOfIntent['comments']>;
}>;

export const UPLOAD_NOTICE_OF_INTENT_FORM = new InjectionToken<UploadNoticeOfIntentFormModel>(
  'Upload notice of intent form',
);

export const UploadNoticeOfIntentFormProvider: Provider = {
  provide: UPLOAD_NOTICE_OF_INTENT_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const noticeOfIntent = requestTaskStore.select(noticeOfIntentQuery.selectNoticeOfIntent)();
    const attachments = requestTaskStore.select(noticeOfIntentQuery.selectNonComplianceAttachments)();

    return fb.group({
      noticeOfIntentFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        noticeOfIntent?.noticeOfIntentFile,
        attachments ?? {},
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        true,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
      comments: fb.control(noticeOfIntent?.comments ?? null, [
        GovukValidators.maxLength(10000, 'The comments should not be more than 10000 characters'),
      ]),
    });
  },
};
