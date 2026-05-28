import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AppealOutcome, AppealOutcomeTribunalDecision } from './types';

export const APPEAL_OUTCOME_TRIBUNAL_DECISION_LABELS: Record<AppealOutcomeTribunalDecision, string> = {
  APPEAL_ALLOWED: 'Appeal allowed',
  APPEAL_DISMISSED: 'Appeal dismissed',
};

export function toAppealOutcomeSummaryData(
  appealOutcome: AppealOutcome,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const changeLink = '../provide-details';
  const datePipe = new DatePipe('en-GB');

  return new SummaryFactory()
    .addSection('')
    .addRow('What was the appeals outcome?', APPEAL_OUTCOME_TRIBUNAL_DECISION_LABELS[appealOutcome?.tribunalDecision], {
      change: isEditable,
      changeLink,
    })
    .addRow('Date of appeal outcome', datePipe.transform(appealOutcome?.appealOutcomeDate, 'd MMM yyyy'), {
      change: isEditable,
      changeLink,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([appealOutcome?.file], attachments ?? {}),
        downloadUrl,
      ),
      { change: isEditable, changeLink },
    )
    .addTextAreaRow('Comments', appealOutcome?.comments ?? '', { change: isEditable, changeLink })
    .create();
}
