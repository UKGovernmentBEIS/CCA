import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE } from '../../../types';

export type PreAuditReviewRequestedDocumentsFormModel = FormGroup<{
  auditMaterialReceivedDate: FormControl<Date>;
  processFlowMapsFile: FormControl<UuidFilePair>;
  manufacturingProcessFile: FormControl<UuidFilePair>;
  annotatedSitePlansFile: FormControl<UuidFilePair>;
  eligibleProcessFile: FormControl<UuidFilePair>;
  directlyAssociatedActivitiesFile: FormControl<UuidFilePair>;
  seventyPerCentRuleEvidenceFile: FormControl<UuidFilePair>;
  baseYearTargetPeriodEvidenceFiles: FormControl<UuidFilePair[]>;
  additionalDocuments: FormControl<UuidFilePair[]>;
  additionalInformation: FormControl<string>;
}>;

export const PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_FORM = new InjectionToken<PreAuditReviewRequestedDocumentsFormModel>(
  'Pre-audit review requested documents form',
);

export const PreAuditReviewAuditReasonFormProvider: Provider = {
  provide: PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const requestedDocuments = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.requestedDocuments;
    const attachments = store.select(preAuditReviewQuery.selectFacilityAuditAttachments)();

    const requestTaskType = store.select(requestTaskQuery.selectRequestTaskType)();

    const processFlowMapsFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.processFlowMapsFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const manufacturingProcessFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.manufacturingProcessFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const annotatedSitePlansFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.annotatedSitePlansFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const eligibleProcessFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.eligibleProcessFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const directlyAssociatedActivitiesFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.directlyAssociatedActivitiesFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const seventyPerCentRuleEvidenceFileControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.seventyPerCentRuleEvidenceFile,
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const baseYearTargetPeriodEvidenceFilesControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.baseYearTargetPeriodEvidenceFiles || [],
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    const additionalDocumentsControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      requestedDocuments?.additionalDocuments || [],
      attachments || {},
      PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    return fb.group(
      {
        auditMaterialReceivedDate: fb.control(
          requestedDocuments?.auditMaterialReceivedDate
            ? new Date(requestedDocuments?.auditMaterialReceivedDate)
            : null,
          { validators: [GovukValidators.required('The date must be today or in the past')] },
        ),
        processFlowMapsFile: processFlowMapsFileControl,
        manufacturingProcessFile: manufacturingProcessFileControl,
        annotatedSitePlansFile: annotatedSitePlansFileControl,
        eligibleProcessFile: eligibleProcessFileControl,
        directlyAssociatedActivitiesFile: directlyAssociatedActivitiesFileControl,
        seventyPerCentRuleEvidenceFile: seventyPerCentRuleEvidenceFileControl,
        baseYearTargetPeriodEvidenceFiles: baseYearTargetPeriodEvidenceFilesControl,
        additionalDocuments: additionalDocumentsControl,
        additionalInformation: fb.control(requestedDocuments?.additionalInformation ?? null),
      },
      { validators: [atLeastOneFileUploadedValidator()], updateOn: 'submit' },
    );
  },
};

function atLeastOneFileUploadedValidator(): ValidatorFn {
  return (group: PreAuditReviewRequestedDocumentsFormModel): ValidationErrors | null => {
    if (!group || !(group instanceof FormGroup)) return null;

    if (
      !group.controls.processFlowMapsFile.value &&
      !group.controls.manufacturingProcessFile.value &&
      !group.controls.annotatedSitePlansFile.value &&
      !group.controls.eligibleProcessFile.value &&
      !group.controls.directlyAssociatedActivitiesFile.value &&
      !group.controls.seventyPerCentRuleEvidenceFile.value &&
      group.controls.baseYearTargetPeriodEvidenceFiles.value?.length === 0 &&
      group.controls.additionalDocuments.value?.length === 0
    ) {
      return { atLeastOneFileUploaded: 'At least one file must be uploaded in order to proceed' };
    }

    return null;
  };
}
