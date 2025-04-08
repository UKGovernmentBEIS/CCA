import { SummaryData, SummaryFactory } from '@shared/components';
import { NoticeRecipientsTypePipe } from '@shared/pipes';
import { transformFileInfoToDownloadableFile } from '@shared/utils';

import { TargetUnitMoaGeneratedRequestActionPayload } from 'cca-api';

export function toTargetUnitMoaGeneratedSummaryData(payload: TargetUnitMoaGeneratedRequestActionPayload): SummaryData {
  const noticeRecipientsPipe = new NoticeRecipientsTypePipe();

  const recipients = payload?.recipients?.map(
    (recipient) => `${recipient.name}, ${noticeRecipientsPipe.transform(recipient.recipientType)}, ${recipient.email}`,
  );

  return new SummaryFactory()
    .addSection('Details')
    .addRow('Payment request ID', payload?.paymentRequestId)
    .addRow('Charging year', String(payload?.chargingYear))
    .addRow('Transaction ID', payload?.transactionId)
    .addFileListRow(
      'Payment requests notice',
      transformFileInfoToDownloadableFile(payload?.moaDocument, './file-download'),
    )

    .addSection('Recipients')
    .addRow('Users', recipients)
    .create();
}
