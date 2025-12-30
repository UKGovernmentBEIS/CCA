import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAdminTerminationFinalDecisionType } from '@shared/pipes';
import { fileUtils } from '@shared/utils';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

export function toFinalDecisionReasonSummaryData(
  adminTerminationFinalDecisionReasonDetails: AdminTerminationFinalDecisionReasonDetails,
  adminTerminationFinalDecisionAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../actions')
    .addRow(
      'Decision',
      transformAdminTerminationFinalDecisionType(adminTerminationFinalDecisionReasonDetails.finalDecisionType),
      {
        change: isEditable,
      },
    )

    .addSection('', 'reason-details')
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
