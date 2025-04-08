import { OverallDecisionWizardStep } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { transformAttachmentsAndFileUUIDsToDownloadableFiles } from '@shared/utils';

import { Determination } from 'cca-api';

export function toOverallDecisionSummaryData(
  determination: Determination,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
) {
  const decisionValue = determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';
  const factory = new SummaryFactory().addSection('').addRow('Decision', decisionValue, {
    change: isEditable,
    changeLink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`,
  });
  if (determination.type === 'REJECTED') {
    factory.addRow('Rejection reason', determination.reason, {
      change: isEditable,
      changeLink: `../${OverallDecisionWizardStep.EXPLANATION}`,
      prewrap: true,
    });
  }
  return factory
    .addRow('Additional information', determination.additionalInformation, {
      change: isEditable,
      changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      prewrap: true,
    })
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(determination.files, attachments, downloadUrl),
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    )
    .create();
}
