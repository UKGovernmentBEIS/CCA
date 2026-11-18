import { boolToString, OverallDecisionWizardStep } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { VariationRegulatorLedDetermination } from 'cca-api';

type ToOperatorAssentDecisionSummaryDataArgs = {
  determination: VariationRegulatorLedDetermination;
  attachments: Record<string, string>;
  downloadUrl: string;
  isEditable: boolean;
};

function toSummaryData(
  determination: VariationRegulatorLedDetermination,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
): SummaryFactory {
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
      'Uploaded files (optional)',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(determination?.files, attachments), downloadUrl),
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    );
}

export function toOperatorAssentDecisionSummaryData(args: ToOperatorAssentDecisionSummaryDataArgs): SummaryData {
  return toSummaryData(args.determination, args.attachments, args.downloadUrl, args.isEditable).create();
}
