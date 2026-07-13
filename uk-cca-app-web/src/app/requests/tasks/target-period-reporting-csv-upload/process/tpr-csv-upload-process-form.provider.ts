import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { PerformanceDataFacilityUpload } from 'cca-api';

import { tprCSVUploadQuery } from '../target-period-reporting-csv-upload.selectors';

export type TprCSVUploadProcessFormModel = FormGroup<{
  targetPeriodType: FormControl<PerformanceDataFacilityUpload['targetPeriodType']>;
  uploadedFiles: FormControl<UuidFilePair[]>;
}>;

export const TPR_CSV_UPLOAD_PROCESS_FORM = new InjectionToken<TprCSVUploadProcessFormModel>(
  'TPR CSV upload process form',
);

export const PerformanceDataUploadProcessFormProvider: Provider = {
  provide: TPR_CSV_UPLOAD_PROCESS_FORM,
  deps: [FormBuilder, RequestTaskStore, AuthStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    authStore: AuthStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const attachments = requestTaskStore.select(tprCSVUploadQuery.selectUploadAttachments)();
    const performanceDataUpload = requestTaskStore.select(tprCSVUploadQuery.selectPerformanceDataUpload)();
    const assigneeUserId = requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();
    const isUserAssignee = authStore.select(selectUserId)() === assigneeUserId;

    if (isUserAssignee) {
      return fb.group({
        targetPeriodType: fb.control(performanceDataUpload?.targetPeriodType ?? null, [
          GovukValidators.required('Please select an option'),
        ]),
        uploadedFiles: requestTaskFileService.buildFormControl(
          requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
          performanceDataUpload?.files || [],
          attachments,
          'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_ATTACH_REPORT',
          true,
          false,
        ),
      });
    }

    return fb.group({
      targetPeriodType: fb.control({ value: performanceDataUpload?.targetPeriodType ?? null, disabled: true }),
    });
  },
};
