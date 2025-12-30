import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { underlyingAgreementActivationQuery } from '../../../una-activation.selectors';

export type UnderlyingAgreementActivationDetailsFormModel = FormGroup<{
  evidenceFiles: FormControl<UuidFilePair[]>;
  comments: FormControl<UnderlyingAgreementActivationDetails['comments']>;
}>;

export const PROVIDE_EVIDENCE_DETAILS_FORM = new InjectionToken<UnderlyingAgreementActivationDetailsFormModel>(
  'Provide Evidence Details Form',
);

export const ProvideEvidenceDetailsFormProvider: Provider = {
  provide: PROVIDE_EVIDENCE_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const details = requestTaskStore.select(underlyingAgreementActivationQuery.selectDetails)();
    const attachments = requestTaskStore.select(underlyingAgreementActivationQuery.selectAttachments)();

    return fb.group({
      evidenceFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        details?.evidenceFiles || [],
        attachments,
        'UNDERLYING_AGREEMENT_ACTIVATION_UPLOAD_ATTACHMENT',
        true,
        !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      ),
      comments: fb.control(details?.comments ?? null, [
        GovukValidators.maxLength(10000, 'The provide comments should not be more than 10000 characters'),
      ]),
    });
  },
};
