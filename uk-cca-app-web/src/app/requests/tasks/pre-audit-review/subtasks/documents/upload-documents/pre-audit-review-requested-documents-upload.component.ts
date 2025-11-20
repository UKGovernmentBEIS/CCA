import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { FileInputComponent, MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK } from '../../../types';
import {
  PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_FORM,
  PreAuditReviewAuditReasonFormProvider,
  PreAuditReviewRequestedDocumentsFormModel,
} from './pre-audit-review-requested-documents-upload-form.provider';

@Component({
  selector: 'cca-pre-audit-review-requested-documents-upload',
  templateUrl: './pre-audit-review-requested-documents-upload.component.html',
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    TextareaComponent,
    DateInputComponent,
    FileInputComponent,
    MultipleFileInputComponent,
  ],
  providers: [PreAuditReviewAuditReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewRequestedDocumentsUploadComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  protected readonly today = new Date();

  protected getDownloadUrl(uuid: string) {
    return ['../../file-download', uuid];
  }

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly form = inject<PreAuditReviewRequestedDocumentsFormModel>(
    PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_FORM,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(preAuditReviewQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(preAuditReviewQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: PreAuditReviewSubmitRequestTaskPayload,
  form: PreAuditReviewRequestedDocumentsFormModel,
): PreAuditReviewSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.preAuditReviewDetails = {
      ...draft?.preAuditReviewDetails,
      requestedDocuments: {
        auditMaterialReceivedDate: form.value.auditMaterialReceivedDate?.toISOString(),
        processFlowMapsFile: fileUtils.toUUIDs([form.value.processFlowMapsFile])[0],
        manufacturingProcessFile: fileUtils.toUUIDs([form.value.manufacturingProcessFile])[0],
        annotatedSitePlansFile: fileUtils.toUUIDs([form.value.annotatedSitePlansFile])[0],
        eligibleProcessFile: fileUtils.toUUIDs([form.value.eligibleProcessFile])[0],
        directlyAssociatedActivitiesFile: fileUtils.toUUIDs([form.value.directlyAssociatedActivitiesFile])[0],
        seventyPerCentRuleEvidenceFile: fileUtils.toUUIDs([form.value.seventyPerCentRuleEvidenceFile])[0],
        baseYearTargetPeriodEvidenceFiles: fileUtils.toUUIDs(form.value.baseYearTargetPeriodEvidenceFiles),
        additionalDocuments: fileUtils.toUUIDs(form.value.additionalDocuments),
        additionalInformation: form.value.additionalInformation,
      },
    };
  });
}
