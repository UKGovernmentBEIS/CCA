import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NonComplianceAppealDetails } from 'cca-api';

export function toAppealDetailsSummaryData(
  appealDetails: NonComplianceAppealDetails | undefined,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const datePipe = new GovukDatePipe();
  const changeLink = '../provide-details';

  return new SummaryFactory()
    .addSection('')
    .addRow('When was the appeal registered?', datePipe.transform(appealDetails?.registrationDate, 'date'), {
      change: isEditable,
      changeLink,
    })
    .addFileListRow(
      'Appeal file',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments(appealDetails?.files, attachments ?? {}), downloadUrl),
      { change: isEditable, changeLink },
    )
    .addTextAreaRow('Comments', appealDetails?.comments ?? '', {
      change: isEditable,
      changeLink,
    })
    .create();
}
