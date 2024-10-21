import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { transformAttachmentsToFilesWithUUIDs, transformFilesToUUIDsList } from '@shared/utils';
import { textFieldValidators } from '@shared/validators';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

import { AdminTerminationFinalDecisionQuery } from '../../+state/admin-termination-final-decision.selectors';

export type FinalDecisionReasonFormModel = FormGroup<{
  explanation: FormControl<AdminTerminationFinalDecisionReasonDetails['explanation']>;
  relevantFiles?: FormControl<UuidFilePair[]>;
}>;

export const FINAL_DECISION_REASON_FORM = new InjectionToken<FinalDecisionReasonFormModel>(
  'Final decision reason form',
);

export const FinalDecisionReasonFormProvider: Provider = {
  provide: FINAL_DECISION_REASON_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const reasonDetails = requestTaskStore.select(
      AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
    )();

    const attachments = requestTaskStore.select(
      AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionAttachments,
    )();

    const files = transformAttachmentsToFilesWithUUIDs(reasonDetails.relevantFiles, attachments);

    return fb.group({
      explanation: fb.control(reasonDetails.explanation, textFieldValidators('reason why of your decision')),
      relevantFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(files),
        attachments,
        'ADMIN_TERMINATION_UPLOAD_ATTACHMENT',
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
