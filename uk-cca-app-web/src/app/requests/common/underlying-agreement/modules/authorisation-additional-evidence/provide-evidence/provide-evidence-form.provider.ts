import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';
import { transformAttachmentsToFilesWithUUIDs, transformFilesToUUIDsList } from '@shared/utils';

import { underlyingAgreementQuery } from '../../../+state';
import { UPLOAD_SECTION_ATTACHMENT_TYPE } from '../../../underlying-agreement.types';

export type ProvideEvidenceFormModel = FormGroup<{
  authorisation: FormControl<UuidFilePair[]>;
  additionalEvidence: FormControl<UuidFilePair[]>;
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

    const authorisationAttachmentFiles = transformAttachmentsToFilesWithUUIDs(
      additionalEvidence.authorisationAttachmentIds,
      underlyingAgreementSubmitAttachments,
    );

    const additionalEvidenceAttachmentFiles = transformAttachmentsToFilesWithUUIDs(
      additionalEvidence.additionalEvidenceAttachmentIds,
      underlyingAgreementSubmitAttachments,
    );

    return fb.group({
      authorisationAttachmentIds: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(authorisationAttachmentFiles),
        underlyingAgreementSubmitAttachments,
        UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
        true,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
      additionalEvidenceAttachmentIds: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        transformFilesToUUIDsList(additionalEvidenceAttachmentFiles),
        underlyingAgreementSubmitAttachments,
        UPLOAD_SECTION_ATTACHMENT_TYPE[requestTaskType],
        false,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
    });
  },
};
