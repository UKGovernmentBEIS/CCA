import { transformAdminTerminationFinalDecisionType } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsToDownloadableFiles } from '@shared/utils';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

import { AdminTerminationFinalDecisionTerminateAgreementWizardStep } from './admin-termination-final-decision.helper';

export function toFinalDecisionReasonSummaryData(
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
  adminTerminationFinalDecisionAttachments: { [key: string]: string },
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', `../${AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS}`)
    .addRow(
      'Decision',
      transformAdminTerminationFinalDecisionType(adminTerminationFinalDecisionReasonDetails.finalDecisionType),
      {
        change: isEditable,
      },
    )
    .addSection('', `../${AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS}`)
    .addRow('Explain reason', adminTerminationFinalDecisionReasonDetails.explanation, { change: isEditable })
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsToDownloadableFiles(
        adminTerminationFinalDecisionReasonDetails.relevantFiles,
        adminTerminationFinalDecisionAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
