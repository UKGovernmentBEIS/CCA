import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsToDownloadableFiles } from '@shared/utils';

import { AdminTerminationWithdrawReasonDetails } from 'cca-api';

import { ReasonForWithdrawAdminTerminationWizardStep } from './withdraw-admin-termination.types';

export function toWithdrawAdminTerminationReasonSummaryData(
  adminTerminationWithdrawReasonDetails: AdminTerminationWithdrawReasonDetails,
  adminTerminationWithdrawAttachments: { [key: string]: string },
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
      transformAttachmentsToDownloadableFiles(
        adminTerminationWithdrawReasonDetails.relevantFiles,
        adminTerminationWithdrawAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
