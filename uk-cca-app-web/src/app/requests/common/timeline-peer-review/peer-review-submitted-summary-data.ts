import { PeerReviewDecisionPipe } from '@requests/common';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { PeerReviewDecisionActionPayload } from './peer-review-submitted.component.selectors';

export function toPeerReviewSummaryData(payload: PeerReviewDecisionActionPayload, submitter: string) {
  const peerReviewDecisionPipe = new PeerReviewDecisionPipe();

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Decision', peerReviewDecisionPipe.transform(payload?.decision?.type))
    .addTextAreaRow('Supporting notes', payload?.decision?.notes)
    .addRow('Peer reviewer', submitter)
    .addFileListRow('Uploaded files', fileUtils.toDownloadableFiles(payload?.peerReviewAttachments, './file-download'))
    .create();
}
