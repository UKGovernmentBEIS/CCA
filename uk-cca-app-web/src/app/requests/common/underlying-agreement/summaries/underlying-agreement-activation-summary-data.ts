import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsAndFileUUIDsToDownloadableFiles } from '@shared/utils';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { ProvideEvidenceWizardStep } from '../underlying-agreement.types';

export function toProvideEvidenceSummaryData(
  details: UnderlyingAgreementActivationDetails,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', `../${ProvideEvidenceWizardStep.DETAILS}`)
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(details?.evidenceFiles, attachments, downloadUrl),
      { change: isEditable },
    )
    .addRow('Comments', details?.comments, { change: isEditable, prewrap: true })
    .create();
}
