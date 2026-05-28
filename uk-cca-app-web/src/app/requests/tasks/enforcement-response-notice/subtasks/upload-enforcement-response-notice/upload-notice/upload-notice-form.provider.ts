import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { NonComplianceEnforcementResponseNotice } from 'cca-api';

import { enforcementResponseNoticeQuery } from '../../../enforcement-response-notice.selectors';

export type UploadEnforcementResponseNoticeFormModel = FormGroup<{
  file: FormControl<UuidFilePair>;
  comments: FormControl<NonComplianceEnforcementResponseNotice['comments']>;
}>;

export const UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_FORM = new InjectionToken<UploadEnforcementResponseNoticeFormModel>(
  'Upload enforcement response notice form',
);

export const UploadEnforcementResponseNoticeFormProvider: Provider = {
  provide: UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const enforcementResponseNotice = requestTaskStore.select(
      enforcementResponseNoticeQuery.selectEnforcementResponseNotice,
    )();
    const attachments = requestTaskStore.select(enforcementResponseNoticeQuery.selectNonComplianceAttachments)();

    return fb.group({
      file: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        enforcementResponseNotice?.file,
        attachments ?? {},
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        true,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
      comments: fb.control(enforcementResponseNotice?.comments ?? null, [
        GovukValidators.maxLength(10000, 'The comments should not be more than 10000 characters'),
      ]),
    });
  },
};
