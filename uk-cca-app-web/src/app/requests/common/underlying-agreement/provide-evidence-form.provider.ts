import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { underlyingAgreementQuery } from './+state/underlying-agreement.selectors';
import { getAttachmentType, UPLOAD_SECTION_ATTACHMENT_TYPE } from './types';

export type ProvideEvidenceFormModel = FormGroup<{
  authorisationAttachmentIds: FormControl<UuidFilePair[]>;
  additionalEvidenceAttachmentIds: FormControl<UuidFilePair[]>;
}>;

export const PROVIDE_EVIDENCE_FORM = new InjectionToken<ProvideEvidenceFormModel>('Provide evidence form');

export const ProvideEvidenceFormProvider: Provider = {
  provide: PROVIDE_EVIDENCE_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const requestTaskFileService = inject(RequestTaskFileService);

    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();

    const additionalEvidence = requestTaskStore.select(
      underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
    )();

    const underlyingAgreementSubmitAttachments = requestTaskStore.select(
      underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments,
    )();

    return fb.group({
      authorisationAttachmentIds: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        additionalEvidence.authorisationAttachmentIds || [],
        underlyingAgreementSubmitAttachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
      additionalEvidenceAttachmentIds: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        additionalEvidence.additionalEvidenceAttachmentIds || [],
        underlyingAgreementSubmitAttachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
