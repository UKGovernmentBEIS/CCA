import { transformAdminTerminationReason } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AdminTerminationReasonDetails } from 'cca-api';

export function toAdminTerminationReasonSummaryData(
  adminTerminationReasonDetails: AdminTerminationReasonDetails,
  adminTerminationSubmitAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../reason-details')
    .addTextAreaRow('Termination reason', transformAdminTerminationReason(adminTerminationReasonDetails.reason), {
      change: isEditable,
    })
    .addTextAreaRow('Explain why the account is being terminated', adminTerminationReasonDetails.explanation, {
      change: isEditable,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(adminTerminationReasonDetails.relevantFiles, adminTerminationSubmitAttachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
