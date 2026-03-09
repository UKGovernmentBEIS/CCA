import { OverallDecisionWizardStep } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { VariationDetermination } from 'cca-api';

export function toOverallDecisionSummaryData(
  determination: VariationDetermination,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
) {
  const decisionValue = !determination.type ? 'Undecided' : determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  const factory = new SummaryFactory().addSection('').addRow('Decision', decisionValue, {
    change: isEditable,
    changeLink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`,
  });

  if (determination.type === 'REJECTED') {
    factory.addTextAreaRow('Rejection reason', determination.reason, {
      change: isEditable,
      changeLink: `../${OverallDecisionWizardStep.EXPLANATION}`,
    });
  }

  // Show whether the variation impacts the agreement when available (variation review only)
  if (determination.type === 'ACCEPTED' && typeof determination?.variationImpactsAgreement === 'boolean') {
    factory.addRow(
      'Does this variation result in changes to the current underlying agreement?',
      determination?.variationImpactsAgreement ? 'Yes' : 'No',
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    );
  }

  return factory
    .addTextAreaRow('Additional information', determination?.additionalInformation, {
      change: isEditable,
      changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(determination?.files, attachments), downloadUrl),
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    )
    .create();
}
