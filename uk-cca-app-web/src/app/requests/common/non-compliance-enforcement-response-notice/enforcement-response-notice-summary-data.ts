import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NonComplianceEnforcementResponseNotice } from 'cca-api';

export const ENFORCEMENT_RESPONSE_NOTICE_TYPE_LABELS: Record<NonComplianceEnforcementResponseNotice['type'], string> = {
  PENALTY: 'Penalty notice',
  PENALTY_WAIVER: 'Penalty waiver notice',
};

export function toEnforcementResponseNoticeSummaryData(
  enforcementResponseNotice: NonComplianceEnforcementResponseNotice | undefined,
  nonComplianceAttachments: Record<string, string> | undefined,
  isEditable: boolean,
  downloadUrl: string,
  isPenaltyReissue = false,
): SummaryData {
  const summary = new SummaryFactory().addSection('', '../upload-enforcement-response-notice');

  if (!isPenaltyReissue) {
    summary.addRow(
      'Type of enforcement response notice',
      transformEnforcementResponseNoticeType(enforcementResponseNotice?.type),
      {
        change: isEditable,
        changeLink: '../enforcement-type',
      },
    );
  }

  return summary
    .addFileListRow(
      'Upload file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([enforcementResponseNotice?.file], nonComplianceAttachments ?? {}),
        downloadUrl,
      ),
      { change: isEditable, changeLink: '../upload-notice' },
    )
    .addTextAreaRow('Your comments', enforcementResponseNotice?.comments ?? '', {
      change: isEditable,
      changeLink: '../upload-notice',
    })
    .create();
}

function transformEnforcementResponseNoticeType(
  type: NonComplianceEnforcementResponseNotice['type'] | undefined,
): string {
  return type ? ENFORCEMENT_RESPONSE_NOTICE_TYPE_LABELS[type] : '';
}
