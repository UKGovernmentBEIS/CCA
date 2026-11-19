import { SummaryData, SummaryFactory } from '@shared/components';
import { boolToString, fileUtils, formatCalendarDate } from '@shared/utils';

import { NonComplianceConclusion } from 'cca-api';

export function toNonComplianceConclusionSummaryData(
  conclusion: NonComplianceConclusion,
  nonComplianceAttachments: Record<string, string> | undefined,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const factory = new SummaryFactory();
  const details = conclusion?.details;

  const changeLink = '../provide-details';

  factory
    .addSection('Conclusion details')
    .addRow('Has compliance been restored?', boolToString(details?.complianceRestored), {
      change: isEditable,
      changeLink,
    });

  if (details?.complianceRestored) {
    factory.addRow(
      'When did the operator become compliant?',
      formatCalendarDate(details.complianceRestoredDate, 'dd MMM yyyy') ?? '',
      {
        change: isEditable,
        changeLink,
      },
    );
  }

  factory.addRow('Has the operator paid the penalty?', boolToString(details?.penaltyPaid), {
    change: isEditable,
    changeLink,
  });

  if (details?.penaltyPaid) {
    factory.addRow('When did the operator pay?', formatCalendarDate(details.penaltyPaymentDate, 'dd MMM yyyy') ?? '', {
      change: isEditable,
      changeLink,
    });
  }

  factory
    .addTextAreaRow('Your comments on the status of compliance', details?.comment ?? '', {
      change: isEditable,
      changeLink,
    })
    .addRow('Would you like to reissue or withdraw the penalty?', penaltyOutcomeLabel(details?.penaltyOutcome), {
      change: isEditable,
      changeLink,
    });

  if (details?.penaltyOutcome === 'WITHDRAW') {
    factory
      .addSection('Withdrawal notice')
      .addFileListRow(
        'Upload file',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments([conclusion?.withdrawNotice?.file], nonComplianceAttachments ?? {}),
          downloadUrl,
        ),
        { change: isEditable, changeLink: '../provide-withdrawal-notice' },
      )
      .addTextAreaRow('Comments', conclusion?.withdrawNotice?.comments ?? '', {
        change: isEditable,
        changeLink: '../provide-withdrawal-notice',
      });
  }

  return factory.create();
}

function penaltyOutcomeLabel(outcome: NonComplianceConclusion['details']['penaltyOutcome']): string {
  switch (outcome) {
    case 'REISSUE':
      return 'Reissue';
    case 'WITHDRAW':
      return 'Withdraw';
    case 'NONE':
      return 'None of the above';
    default:
      return '';
  }
}
