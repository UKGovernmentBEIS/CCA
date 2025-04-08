import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { transformAttachmentsToFilesWithUUIDs, transformFilesToUUIDsList } from '@shared/utils';

import { performanceDataUploadQuery } from '../+state/performance-data-upload-selectors';

export type UploadProcessPerformanceDataFormModel = FormGroup<{
  targetPeriodType: FormControl<PerformanceDataTargetPeriodEnum>;
  uploadedFiles: FormControl<UuidFilePair[]>;
}>;

export const UPLOAD_PROCESS_PERFORMANCE_DATA_FORM = new InjectionToken<UploadProcessPerformanceDataFormModel>(
  'Upload process performance data form',
);

export const PerformanceDataUploadProcessFormProvider: Provider = {
  provide: UPLOAD_PROCESS_PERFORMANCE_DATA_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const requestTaskFileService = inject(RequestTaskFileService);
    const attachments = requestTaskStore.select(performanceDataUploadQuery.selectPerformanceDataUploadAttachments)();
    const performanceDataUpload = requestTaskStore.select(performanceDataUploadQuery.selectPerformanceDataUpload)();

    const uploadedFiles = transformAttachmentsToFilesWithUUIDs(
      performanceDataUpload?.reportPackages ?? [],
      attachments,
    );

    return fb.group({
      targetPeriodType: fb.control(PerformanceDataTargetPeriodEnum.TP6, [
        GovukValidators.required('Please select an option'),
      ]),
      uploadedFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(uploadedFiles),
        attachments,
        'PERFORMANCE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE',
        true,
        false,
      ),
    });
  },
};
