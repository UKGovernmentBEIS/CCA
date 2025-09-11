import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

export function toProvideEvidenceSummaryData(
  details: UnderlyingAgreementActivationDetails,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  return new SummaryFactory()
    .addSection('', '../details')
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(details?.evidenceFiles, attachments), downloadUrl),
      { change: isEditable },
    )
    .addTextAreaRow('Comments', details?.comments, { change: isEditable })
    .create();
}
