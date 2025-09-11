import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { underlyingAgreementVariationActivationQuery } from '../../../+state/una-variation-activation.selectors';

export type UnderlyingAgreementActivationDetailsFormModel = {
  evidenceFiles: FormControl<UuidFilePair[]>;
  comments: FormControl<UnderlyingAgreementActivationDetails['comments']>;
};

export const PROVIDE_EVIDENCE_DETAILS_FORM = new InjectionToken<UnderlyingAgreementActivationDetailsFormModel>(
  'Provide Evidence Details Form',
);

export const ProvideEvidenceDetailsFormProvider: Provider = {
  provide: PROVIDE_EVIDENCE_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const activationDetails = requestTaskStore.select(
      underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
    )();

    const attachments = requestTaskStore.select(
      underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationAttachments,
    )();

    return fb.group({
      evidenceFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        activationDetails?.evidenceFiles || [],
        attachments,
        'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_UPLOAD_ATTACHMENT',
        true,
        false,
      ),
      comments: fb.control(activationDetails?.comments ?? null, [
        GovukValidators.maxLength(10000, 'The provide comments should not be more than 10000 characters'),
      ]),
    });
  },
};
