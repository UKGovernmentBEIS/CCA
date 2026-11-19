import { SummaryData, SummaryFactory } from '@shared/components';
import { boolToString, formatCalendarDate } from '@shared/utils';

import { AuditDetermination } from 'cca-api';

export function toPreAuditReviewDeterminationSummaryData(
  determination: AuditDetermination,
  isEditable: boolean,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../review-determination')
    .addRow('Review completion date', formatCalendarDate(determination?.reviewCompletionDate, 'dd MMM yyyy'), {
      change: isEditable,
    })
    .addTextAreaRow(
      'Is a more detailed audit of this facility needed?',
      boolToString(determination?.furtherAuditNeeded),
      {
        change: isEditable,
      },
    )
    .addTextAreaRow('Review comments', determination?.reviewComments, {
      change: isEditable,
    })
    .create();
}
