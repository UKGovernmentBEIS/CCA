import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { textFieldValidators } from '@shared/validators';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

import { adminTerminationWithdrawQuery } from '../../../withdraw-admin-termination.selectors';

export type ReasonForWithdrawAdminTerminationFormModel = FormGroup<{
  explanation: FormControl<AdminTerminationWithdrawReasonDetails['explanation']>;
  relevantFiles?: FormControl<UuidFilePair[]>;
}>;

export const REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM =
  new InjectionToken<ReasonForWithdrawAdminTerminationFormModel>('Reason for withdrawing admin termination form');

export const ReasonForWithdrawAdminTerminationFormProvider: Provider = {
  provide: REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const reasonDetails = requestTaskStore.select(adminTerminationWithdrawQuery.selectReasonDetails)();
    const attachments = requestTaskStore.select(adminTerminationWithdrawQuery.selectAttachments)();

    return fb.group({
      explanation: fb.control(
        reasonDetails.explanation,
        textFieldValidators('reason why you are withdrawing the admin termination', 10000),
      ),
      relevantFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        reasonDetails.relevantFiles,
        attachments,
        'ADMIN_TERMINATION_UPLOAD_ATTACHMENT',
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
