import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { CcaPeerReviewDecisionSubmittedRequestActionPayload } from 'cca-api';

export function toDecisionDetailsSummaryData(
  payload: CcaPeerReviewDecisionSubmittedRequestActionPayload,
  submitter: string,
): SummaryData {
  const decision = payload.decision;

  return new SummaryFactory()
    .addSection('Details')
    .addRow(
      'Decision',
      decision.type === 'AGREE' ? 'I agree with the determination' : 'I disagree with the determination',
    )
    .addTextAreaRow('Supporting notes', decision?.notes)
    .addRow('Peer reviewer', submitter)
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(decision.files, payload.peerReviewAttachments),
        'file-download',
      ),
    )
    .create();
}
