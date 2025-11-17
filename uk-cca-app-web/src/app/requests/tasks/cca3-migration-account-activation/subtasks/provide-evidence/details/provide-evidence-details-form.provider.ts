import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails } from 'cca-api';

import { cca3MigrationAccountActivationQuery } from '../../../+state/cca3-migration-account-activation.selectors';

export type Cca3MigrationAccountActivationDetailsFormModel = FormGroup<{
  evidenceFiles: FormControl<UuidFilePair[]>;
  comments: FormControl<Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails['comments']>;
}>;

export const CCA3_MIGRATION_PROVIDE_EVIDENCE_DETAILS_FORM =
  new InjectionToken<Cca3MigrationAccountActivationDetailsFormModel>('Provide Evidence Details Form');

export const ProvideEvidenceDetailsFormProvider: Provider = {
  provide: CCA3_MIGRATION_PROVIDE_EVIDENCE_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const activationDetails = requestTaskStore.select(
      cca3MigrationAccountActivationQuery.selectCca3MigrationAccountActivationDetails,
    )();

    const attachments = requestTaskStore.select(
      cca3MigrationAccountActivationQuery.selectCca3MigrationAccountActivationAttachments,
    )();

    return fb.group({
      evidenceFiles: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        activationDetails?.evidenceFiles || [],
        attachments,
        'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_UPLOAD_ATTACHMENT',
        true,
        false,
      ),
      comments: fb.control(activationDetails?.comments ?? null, [
        GovukValidators.maxLength(10000, 'The provide comments should not be more than 10000 characters'),
      ]),
    });
  },
};
