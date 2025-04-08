import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsAndFileUUIDsToDownloadableFiles } from '@shared/utils';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

import { ReasonForWithdrawAdminTerminationWizardStep } from './withdraw-admin-termination.types';

export function toWithdrawAdminTerminationReasonSummaryData(
  adminTerminationWithdrawReasonDetails: AdminTerminationWithdrawReasonDetails,
  adminTerminationWithdrawAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', `../${ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS}`)
    .addRow(
      'Explain why you are withdrawing the admin termination',
      adminTerminationWithdrawReasonDetails.explanation,
      {
        change: isEditable,
      },
    )
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        adminTerminationWithdrawReasonDetails.relevantFiles,
        adminTerminationWithdrawAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
