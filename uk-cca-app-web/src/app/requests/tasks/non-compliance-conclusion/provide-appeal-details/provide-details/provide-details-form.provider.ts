import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { FileUploadEvent } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { NonComplianceAppealDetails } from 'cca-api';

import { ProvideAppealDetailsStore } from '../+state';

export type ProvideAppealDetailsFormModel = FormGroup<{
  registrationDate: FormControl<NonComplianceAppealDetails['registrationDate'] | Date | null>;
  files: FormControl<FileUploadEvent | FileUploadEvent[]>;
  comments: FormControl<NonComplianceAppealDetails['comments']>;
}>;

export const PROVIDE_APPEAL_DETAILS_FORM = new InjectionToken<ProvideAppealDetailsFormModel>(
  'Provide appeal details form',
);

export const ProvideAppealDetailsFormProvider: Provider = {
  provide: PROVIDE_APPEAL_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, ProvideAppealDetailsStore],
  useFactory: createProvideAppealDetailsForm,
};

function createProvideAppealDetailsForm(
  fb: FormBuilder,
  requestTaskStore: RequestTaskStore,
  requestTaskFileService: RequestTaskFileService,
  provideAppealDetailsStore: ProvideAppealDetailsStore,
): ProvideAppealDetailsFormModel {
  const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const appealDetails = provideAppealDetailsStore.state.appealDetails;

  return fb.group({
    registrationDate: fb.control<NonComplianceAppealDetails['registrationDate'] | Date | null>(
      appealDetails?.registrationDate ? new Date(appealDetails.registrationDate) : null,
      [
        GovukValidators.required('Enter the date the appeal was registered'),
        futureDateValidator('The date must be today or in the past'),
      ],
    ),
    files: requestTaskFileService.buildFormControl(
      requestTaskId,
      appealDetails?.files ?? [],
      provideAppealDetailsStore.state.attachments,
      'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
      false,
      !isEditable,
    ),
    comments: fb.control(appealDetails?.comments ?? null, [
      GovukValidators.maxLength(10000, 'Comments must be 10000 characters or less'),
    ]),
  });
}
