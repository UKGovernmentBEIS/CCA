import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { textFieldValidators } from '@shared/validators';

import { AdminTerminationReasonDetails } from 'cca-api';

import { adminTerminationQuery } from '../../../admin-termination.selectors';

export type ReasonForAdminTerminationFormModel = FormGroup<{
  reason: FormControl<AdminTerminationReasonDetails['reason']>;
  explanation: FormControl<AdminTerminationReasonDetails['explanation']>;
  relevantFiles?: FormControl<UuidFilePair[]>;
}>;

export const REASON_FOR_ADMIN_TERMINATION_FORM = new InjectionToken<ReasonForAdminTerminationFormModel>(
  'Reason for admin termination form',
);

export const ReasonForAdminTerminationFormProvider: Provider = {
  provide: REASON_FOR_ADMIN_TERMINATION_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const adminTerminationReasonDetails = requestTaskStore.select(adminTerminationQuery.selectReasonDetails)();
    const adminTerminationSubmitAttachments = requestTaskStore.select(adminTerminationQuery.selectSubmitAttachments)();

    return fb.group({
      reason: fb.control(
        adminTerminationReasonDetails?.reason,
        GovukValidators.required('Select a reason for termination'),
      ),
      explanation: fb.control(
        adminTerminationReasonDetails?.explanation,
        textFieldValidators('reason why you are terminating the agreement', 10000),
      ),
      relevantFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        adminTerminationReasonDetails.relevantFiles,
        adminTerminationSubmitAttachments,
        'ADMIN_TERMINATION_UPLOAD_ATTACHMENT',
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
