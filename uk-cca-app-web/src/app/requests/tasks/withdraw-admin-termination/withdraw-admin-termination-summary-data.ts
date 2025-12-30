import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

export function toWithdrawAdminTerminationReasonSummaryData(
  adminTerminationWithdrawReasonDetails: AdminTerminationWithdrawReasonDetails,
  adminTerminationWithdrawAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../reason-details')
    .addTextAreaRow(
      'Explain why you are withdrawing the admin termination',
      adminTerminationWithdrawReasonDetails.explanation,
      {
        change: isEditable,
      },
    )
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          adminTerminationWithdrawReasonDetails.relevantFiles,
          adminTerminationWithdrawAttachments,
        ),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
