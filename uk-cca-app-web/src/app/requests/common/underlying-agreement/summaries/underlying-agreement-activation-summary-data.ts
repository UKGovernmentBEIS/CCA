import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

type ToProvideEvidenceSummaryDataArgs = {
  details: UnderlyingAgreementActivationDetails;
  attachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
};

export function toProvideEvidenceSummaryData(args: ToProvideEvidenceSummaryDataArgs): SummaryData {
  return new SummaryFactory()
    .addSection('', '../details')
    .addFileListRow(
      'Uploaded files',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(args.details?.evidenceFiles, args.attachments),
        args.downloadUrl,
      ),
      { change: args.isEditable },
    )
    .addTextAreaRow('Comments', args.details?.comments, {
      change: args.isEditable,
    })
    .create();
}
