import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { NonComplianceWithdrawNotice } from 'cca-api';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';

export type ProvideWithdrawalNoticeFormModel = FormGroup<{
  file: FormControl<UuidFilePair>;
  comments: FormControl<NonComplianceWithdrawNotice['comments']>;
}>;

export const PROVIDE_WITHDRAWAL_NOTICE_FORM = new InjectionToken<ProvideWithdrawalNoticeFormModel>(
  'Provide withdrawal notice form',
);

export const ProvideWithdrawalNoticeFormProvider: Provider = {
  provide: PROVIDE_WITHDRAWAL_NOTICE_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const withdrawNotice = store.select(nonComplianceConclusionQuery.selectWithdrawNotice)();
    const attachments = store.select(nonComplianceConclusionQuery.selectAttachments)();
    const taskId = store.select(requestTaskQuery.selectRequestTaskId)();
    const isEditable = store.select(requestTaskQuery.selectIsEditable)();

    return fb.group({
      file: requestTaskFileService.buildFormControl(
        taskId,
        withdrawNotice?.file,
        attachments ?? {},
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        true,
        !isEditable,
      ),
      comments: fb.control(withdrawNotice?.comments ?? null, [
        GovukValidators.required('Enter comments'),
        GovukValidators.maxLength(10000, 'Comments must be 10000 characters or less'),
      ]),
    });
  },
};
