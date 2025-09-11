import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAdminTerminationFinalDecisionType } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

import { AdminTerminationFinalDecisionTerminateAgreementWizardStep } from './admin-termination-final-decision.helper';

export function toFinalDecisionReasonSummaryData(
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
  adminTerminationFinalDecisionAttachments: Record<string, string>,
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
    .addTextAreaRow('Explain reason', adminTerminationFinalDecisionReasonDetails.explanation, {
      change: isEditable,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          adminTerminationFinalDecisionReasonDetails.relevantFiles,
          adminTerminationFinalDecisionAttachments,
        ),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
