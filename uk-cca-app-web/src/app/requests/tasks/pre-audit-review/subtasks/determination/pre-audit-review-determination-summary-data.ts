import { DatePipe } from '@angular/common';

import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';

import { AuditDetermination } from 'cca-api';

export function toPreAuditReviewDeterminationSummaryData(
  determination: AuditDetermination,
  isEditable: boolean,
): SummaryData {
  const datePipe = new DatePipe('en-GB');

  return new SummaryFactory()
    .addSection('', '../review-determination')
    .addRow('Review completion date', datePipe.transform(determination?.reviewCompletionDate, 'dd MMM yyyy'), {
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
