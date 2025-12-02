import { DatePipe } from '@angular/common';

import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementFacilityReviewDecision, UnderlyingAgreementReviewDecision } from 'cca-api';

export function addDecisionSummaryData(
  factory: SummaryFactory,
  decision: UnderlyingAgreementReviewDecision,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryFactory {
  return factory
    .addSection('Decision Summary', '../decision')
    .addRow(
      'Decision status',
      decision?.type ? decision.type.slice(0, 1).concat(decision?.type.slice(1).toLowerCase()) : null,
      {
        change: isEditable,
      },
    )
    .addRow('Notes', decision?.details?.notes ?? null, {
      change: isEditable,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(decision?.details?.files, attachments), downloadUrl),
      { change: isEditable },
    );
}

export function addFacilityDecisionSummaryData(
  factory: SummaryFactory,
  decision: UnderlyingAgreementFacilityReviewDecision,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryFactory {
  const datePipe = new DatePipe('en-GB');

  const f = factory.addSection('Decision Summary', '../decision');

  f.addRow(
    'Decision status',
    decision?.type ? decision.type.slice(0, 1).concat(decision?.type.slice(1).toLowerCase()) : null,
    {
      change: isEditable,
    },
  );

  if (decision?.changeStartDate) {
    f.addRow('Start date of paying the subsistence charge fee', datePipe.transform(decision?.startDate, 'dd/MM/yyyy'), {
      change: isEditable,
    });
  }

  return f
    .addRow('Notes', decision?.details?.notes ?? null, {
      change: isEditable,
    })
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(decision?.details?.files, attachments), downloadUrl),
      { change: isEditable },
    );
}
