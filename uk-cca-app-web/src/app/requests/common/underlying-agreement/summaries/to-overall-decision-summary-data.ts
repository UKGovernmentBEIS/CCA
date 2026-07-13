import { OverallDecisionWizardStep } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { VariationDetermination } from 'cca-api';

type ToOverallDecisionSummaryDataArgs = {
  determination: VariationDetermination;
  attachments: Record<string, string>;
  downloadUrl: string;
  isEditable: boolean;
};

function toSummaryData(
  determination: VariationDetermination,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
): SummaryFactory {
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

  if (determination.type === 'ACCEPTED' && typeof determination.variationImpactsAgreement === 'boolean') {
    factory.addRow(
      'Does this variation result in changes to the current underlying agreement?',
      determination.variationImpactsAgreement ? 'Yes' : 'No',
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    );
  }

  return factory
    .addTextAreaRow('Additional information', determination.additionalInformation, {
      change: isEditable,
      changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(determination.files, attachments), downloadUrl),
      {
        change: isEditable,
        changeLink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`,
      },
    );
}

export function toOverallDecisionSummaryData(args: ToOverallDecisionSummaryDataArgs): SummaryData {
  return toSummaryData(args.determination, args.attachments, args.downloadUrl, args.isEditable).create();
}
