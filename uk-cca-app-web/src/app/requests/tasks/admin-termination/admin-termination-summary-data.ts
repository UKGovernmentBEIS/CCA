import { transformAdminTerminationReason } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAttachmentsToDownloadableFiles } from '@shared/utils';

import { AdminTerminationReasonDetails } from 'cca-api';

import { ReasonForAdminTerminationWizardStep } from './admin-termination.types';

export function toAdminTerminationReasonSummaryData(
  adminTerminationReasonDetails: AdminTerminationReasonDetails,
  adminTerminationSubmitAttachments: { [key: string]: string },
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', `../${ReasonForAdminTerminationWizardStep.REASON_DETAILS}`)
    .addRow('Termination reason', transformAdminTerminationReason(adminTerminationReasonDetails.reason), {
      change: isEditable,
      prewrap: true,
    })
    .addRow('Explain why the account is being terminated', adminTerminationReasonDetails.explanation, {
      change: isEditable,
    })
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsToDownloadableFiles(
        adminTerminationReasonDetails.relevantFiles,
        adminTerminationSubmitAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .create();
}
