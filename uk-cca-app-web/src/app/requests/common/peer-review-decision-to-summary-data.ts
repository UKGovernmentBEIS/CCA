import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { CcaPeerReviewDecision } from 'cca-api';

export function peerReviewDecisionToSummaryData(
  decision: CcaPeerReviewDecision,
  attachments: Record<string, string>,
): SummaryData {
  const factory = new SummaryFactory();

  factory.addSection('');

  const decisionText =
    decision.type === 'AGREE' ? 'I agree with the determination' : 'I do not agree with the determination';
  factory.addRow('Decision', decisionText, {
    change: true,
    changeLink: '../',
  });

  factory.addRow('Supporting note', decision.notes || '', {
    change: true,
    changeLink: '../',
  });

  const downloadableFiles = fileUtils.toDownloadableFiles(attachments, '../../../file-download') ?? [];
  factory.addFileListRow('Uploaded files', downloadableFiles, {
    change: true,
    changeLink: '../',
  });

  return factory.create();
}
