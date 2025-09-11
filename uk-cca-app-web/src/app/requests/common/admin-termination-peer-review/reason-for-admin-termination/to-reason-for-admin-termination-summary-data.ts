import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AdminTerminationReasonDetails } from 'cca-api';

import { transformAdminTerminationReason } from '../../admin-termination-reason.pipe';

export function toReasonForAdminTerminationDetailsSummaryData(
  adminTerminationReasonDetails: AdminTerminationReasonDetails,
  attachments: Record<string, string>,
): SummaryData {
  return new SummaryFactory()
    .addSection('Details')
    .addTextAreaRow('Termination reason', transformAdminTerminationReason(adminTerminationReasonDetails.reason))
    .addRow('Explain why the account is being terminated', adminTerminationReasonDetails.explanation)
    .addFileListRow('Uploaded files', fileUtils.toDownloadableFiles(attachments, '../../file-download'))
    .create();
}
