import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { patCsvUploadQuery } from '../performance-account-template-csv-upload.selectors';

export type PatCsvUploadProcessFormModel = FormGroup<{
  uploadedFiles: FormControl<UuidFilePair[]>;
}>;

export const PAT_CSV_UPLOAD_PROCESS_FORM = new InjectionToken<PatCsvUploadProcessFormModel>(
  'PAT CSV upload process form',
);

export const PatCsvUploadProcessFormProvider: Provider = {
  provide: PAT_CSV_UPLOAD_PROCESS_FORM,
  deps: [FormBuilder, RequestTaskStore, AuthStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    authStore: AuthStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const attachments = requestTaskStore.select(patCsvUploadQuery.selectUploadAttachments)();
    const performanceAccountTemplateDataUpload = requestTaskStore.select(
      patCsvUploadQuery.selectPerformanceAccountTemplateDataUpload,
    )();
    const assigneeUserId = requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();
    const isUserAssignee = authStore.select(selectUserId)() === assigneeUserId;

    if (isUserAssignee) {
      return fb.group({
        uploadedFiles: requestTaskFileService.buildFormControl(
          requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
          performanceAccountTemplateDataUpload?.files || [],
          attachments,
          'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACHMENT',
          true,
          false,
        ),
      });
    }

    return fb.group({});
  },
};
