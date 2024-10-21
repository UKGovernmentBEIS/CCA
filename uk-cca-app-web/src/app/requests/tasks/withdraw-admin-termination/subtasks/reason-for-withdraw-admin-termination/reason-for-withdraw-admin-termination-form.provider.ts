import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { transformAttachmentsToFilesWithUUIDs, transformFilesToUUIDsList } from '@shared/utils';
import { textFieldValidators } from '@shared/validators';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

import { AdminTerminationWithdrawQuery } from '../../+state/withdraw-admin-termination.selectors';

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
    const withdrawAdminTerminationReasonDetails = requestTaskStore.select(
      AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails,
    )();

    const withdrawAdminTerminationAttachments = requestTaskStore.select(
      AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationAttachments,
    )();

    const files = transformAttachmentsToFilesWithUUIDs(
      withdrawAdminTerminationReasonDetails.relevantFiles,
      withdrawAdminTerminationAttachments,
    );

    return fb.group({
      explanation: fb.control(
        withdrawAdminTerminationReasonDetails.explanation,
        textFieldValidators('reason why you are withdrawing the admin termination', 10000),
      ),
      relevantFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(files),
        withdrawAdminTerminationAttachments,
        'ADMIN_TERMINATION_UPLOAD_ATTACHMENT',
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
