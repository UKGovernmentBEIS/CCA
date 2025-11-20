import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { AuditDetails } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { AUDIT_DETAILS_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE } from '../../../types';

export type AuditDetailsCorrectiveActionsFormModel = FormGroup<{
  auditTechnique: FormControl<AuditDetails['auditTechnique']>;
  auditDate: FormControl<Date>;
  comments: FormControl<AuditDetails['comments']>;
  finalAuditReportDate: FormControl<Date>;
  auditDocuments: FormControl<UuidFilePair[]>;
}>;

export const AUDIT_DETAILS_FORM = new InjectionToken<AuditDetailsCorrectiveActionsFormModel>('Audit details form');

export const PreAuditReviewAuditReasonFormProvider: Provider = {
  provide: AUDIT_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const auditDetails = store.select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
      ?.auditDetails;

    const attachments = store.select(auditDetailsCorrectiveActionsQuery.selectFacilityAuditAttachments)();

    const requestTaskType = store.select(requestTaskQuery.selectRequestTaskType)();

    const auditDocumentsControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      auditDetails?.auditDocuments || [],
      attachments || {},
      AUDIT_DETAILS_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
      true,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    return fb.group({
      auditTechnique: fb.control(auditDetails?.auditTechnique ?? 'DESK_BASED_INTERVIEW', {
        validators: [GovukValidators.required('Select an audit technique')],
      }),
      auditDate: fb.control(auditDetails?.auditDate ? new Date(auditDetails?.auditDate) : null, {
        validators: [GovukValidators.required('The date must be today or in the past')],
      }),
      comments: fb.control(auditDetails?.comments ?? null, {
        validators: [GovukValidators.required('Enter comments about the audit')],
      }),
      finalAuditReportDate: fb.control(
        auditDetails?.finalAuditReportDate ? new Date(auditDetails?.finalAuditReportDate) : null,
        { validators: [GovukValidators.required('Enter the date of the final audit report')] },
      ),
      auditDocuments: auditDocumentsControl,
    });
  },
};
