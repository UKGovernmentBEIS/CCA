import { boolToString, OverallDecisionWizardStep } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { VariationRegulatorLedDetermination } from 'cca-api';

export function toOperatorAssentDecisionSummaryData(
  determination: VariationRegulatorLedDetermination,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
) {
  return new SummaryFactory()
    .addSection('')
    .addRow(
      'Does this variation result in changes to the current underlying agreement?',
      boolToString(determination?.variationImpactsAgreement),
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    )
    .addTextAreaRow('Additional information (optional)', determination?.additionalInformation, {
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
