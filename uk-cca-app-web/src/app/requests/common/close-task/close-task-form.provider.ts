import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { NonComplianceCloseJustification } from 'cca-api';

import { nonComplianceConclusionQuery } from '../../tasks/non-compliance-conclusion/non-compliance-conclusion.selectors';

export type CloseTaskFormModel = FormGroup<{
  reason: FormControl<NonComplianceCloseJustification['reason']>;
  files: FormControl<UuidFilePair[]>;
}>;

export const CLOSE_TASK_FORM = new InjectionToken<CloseTaskFormModel>('Close task form');

export const CloseTaskFormProvider: Provider = {
  provide: CLOSE_TASK_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const payload = requestTaskStore.select(nonComplianceConclusionQuery.selectPayload)();
    const closeJustification = payload?.closeJustification;
    const attachments = requestTaskStore.select(nonComplianceConclusionQuery.selectAttachments)() ?? {};

    return fb.group({
      reason: fb.nonNullable.control(closeJustification?.reason ?? '', [
        GovukValidators.required('You must provide an explanation'),
        GovukValidators.maxLength(10000, 'The explanation should not be more than 10000 characters'),
      ]),
      files: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        closeJustification?.files ?? [],
        attachments,
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
