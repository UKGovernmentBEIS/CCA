import { InjectionToken, Provider } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators, MessageValidationErrors } from '@netz/govuk-components';
import { FileUploadEvent } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { appealOutcomeQuery } from '../appeal-outcome.selectors';
import { AppealOutcome } from '../types';

export type AppealOutcomeFormModel = FormGroup<{
  tribunalDecision: FormControl<AppealOutcome['tribunalDecision']>;
  appealOutcomeDate: FormControl<AppealOutcome['appealOutcomeDate'] | Date | null>;
  file: FormControl<FileUploadEvent | FileUploadEvent[]>;
  comments: FormControl<AppealOutcome['comments']>;
}>;

export const APPEAL_OUTCOME_FORM = new InjectionToken<AppealOutcomeFormModel>('Appeal outcome form');

export const AppealOutcomeFormProvider: Provider = {
  provide: APPEAL_OUTCOME_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
  ): AppealOutcomeFormModel => {
    const appealOutcome = requestTaskStore.select(appealOutcomeQuery.selectAppealOutcome)();
    const attachments = requestTaskStore.select(appealOutcomeQuery.selectNonComplianceAttachments)();
    const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

    const fileControl = requestTaskFileService.buildFormControl(
      requestTaskId,
      appealOutcome?.file ? [appealOutcome.file] : [],
      attachments ?? {},
      'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
      false,
      !isEditable,
    );
    fileControl.addValidators(singleFileValidator);

    return fb.group({
      tribunalDecision: fb.control(appealOutcome?.tribunalDecision ?? null, [
        GovukValidators.required('Select the appeals outcome'),
      ]),
      appealOutcomeDate: fb.control<AppealOutcome['appealOutcomeDate'] | Date | null>(
        appealOutcome?.appealOutcomeDate ? new Date(appealOutcome.appealOutcomeDate) : null,
        [
          GovukValidators.required('Enter the date of appeal outcome'),
          futureDateValidator('This date must be today or in the past'),
        ],
      ),
      file: fileControl,
      comments: fb.control(appealOutcome?.comments ?? null, [
        GovukValidators.maxLength(10000, 'Comments must be 10000 characters or less'),
      ]),
    });
  },
};

function singleFileValidator(control: AbstractControl<FileUploadEvent | FileUploadEvent[]>): MessageValidationErrors {
  const files = Array.isArray(control.value) ? control.value : control.value ? [control.value] : [];

  return files.length > 1 ? { singleFile: 'Upload only one file' } : null;
}
