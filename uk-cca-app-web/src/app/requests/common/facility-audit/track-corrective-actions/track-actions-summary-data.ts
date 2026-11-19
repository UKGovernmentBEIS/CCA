import { SummaryData, SummaryFactory } from '@shared/components';
import { boolToString, fileUtils, formatCalendarDate } from '@shared/utils';

import { AuditCorrectiveActionResponse } from 'cca-api';

export function toTrackActionSummaryData(
  action: AuditCorrectiveActionResponse,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const factory = new SummaryFactory()
    .addSection('')
    .addTextAreaRow('Corrective action details', action?.details)
    .addRow('Corrective action deadline', formatCalendarDate(action?.deadline, 'dd MMM yyyy'))

    .addSection('Corrective action carried out details')
    .addRow(
      'Are you satisfied the operator carried out this corrective action?',
      boolToString(action?.isActionCarriedOut),
      {
        change: isEditable,
        changeLink: '../is-carried-out',
      },
    );

  if (!action?.isActionCarriedOut) {
    factory.addTextAreaRow('Comments', action?.comments ?? 'Not provided');
  }

  if (action?.isActionCarriedOut) {
    factory
      .addRow('Date carried out', formatCalendarDate(action?.actionCarriedOutDate, 'dd MMM yyyy'), {
        change: isEditable,
        changeLink: '../details',
      })
      .addTextAreaRow('How the corrective action has been carried out', action?.comments, {
        change: isEditable,
        changeLink: '../details',
      })
      .addFileListRow(
        'Uploaded evidence',
        fileUtils.toDownloadableFiles(fileUtils.extractAttachments(action?.evidenceFiles, attachments), downloadUrl),
        { change: isEditable, changeLink: '../details' },
      );
  }

  return factory.create();
}
