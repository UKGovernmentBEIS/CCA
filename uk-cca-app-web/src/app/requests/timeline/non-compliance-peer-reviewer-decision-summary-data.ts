import { PeerReviewDecisionPipe } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { CcaPeerReviewDecisionSubmittedRequestActionPayload } from 'cca-api';

export function toNonCompliancePeerReviewDecisionSummaryData(
  payload: CcaPeerReviewDecisionSubmittedRequestActionPayload,
) {
  const peerReviewDecisionPipe = new PeerReviewDecisionPipe();

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Peer review decision', peerReviewDecisionPipe.transform(payload?.decision?.type))
    .addTextAreaRow('Supporting notes', payload?.decision?.notes)
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(payload?.decision?.files, payload?.peerReviewAttachments),
        './file-download',
      ),
    )
    .create();
}
