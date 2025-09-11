import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { PATUploadQuery } from './+state/pat-selectors';

export type UploadProcessPATFormModel = FormGroup<{
  targetPeriodType: FormControl<PerformanceDataTargetPeriodEnum>;
  uploadedFiles: FormControl<UuidFilePair[]>;
}>;

export const UPLOAD_PROCESS_PAT_FORM = new InjectionToken<UploadProcessPATFormModel>('Upload process pat form');

export const PATUploadProcessFormProvider: Provider = {
  provide: UPLOAD_PROCESS_PAT_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const requestTaskFileService = inject(RequestTaskFileService);
    const attachments = requestTaskStore.select(PATUploadQuery.selectPATUploadAttachments)();
    const patUploadPayload = requestTaskStore.select(PATUploadQuery.selectPATUploadPayload)();

    return fb.group({
      targetPeriodType: fb.control(PerformanceDataTargetPeriodEnum.TP6, [
        GovukValidators.required('Please select an option'),
      ]),
      uploadedFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        patUploadPayload?.reportPackages || [],
        attachments,
        'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE',
        true,
        false,
      ),
    });
  },
};
