import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { NoticeRecipientsTypePipe } from '@shared/pipes';
import { fileUtils, formatGbp } from '@shared/utils';

import { BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { formatHistoricalNumber } from './format-historical-number';

const govukDate = new GovukDatePipe();
const recipientType = new NoticeRecipientsTypePipe();

export function toBuyoutSurplusAccountSummaryData(
  payload: BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload | undefined,
): SummaryData {
  if (!payload) return [];

  const summary = new SummaryFactory().addSection('Details').addRow('Run ID', payload.runId);
  // Missing notice metadata must not hide the supplied historical transaction summary.
  const transactions = [...(payload.summary ?? [])].sort(
    (left, right) =>
      left.targetPeriodType.localeCompare(right.targetPeriodType, 'en', { numeric: true }) ||
      left.chargeType.localeCompare(right.chargeType),
  );

  for (const item of transactions) {
    const isBuyout = item.chargeType === 'FEE';
    summary.addSection(`${item.targetPeriodType} ${isBuyout ? 'Buy-out' : 'Refund'}`);

    if (isBuyout) summary.addRow('Cost (GBP / tCO₂e)', formatGbp(item.targetPeriodBuyOutCost));

    summary
      .addFileListRow(
        isBuyout ? 'Buy-out MoA file' : 'Refund letter',
        fileUtils.toDownloadableDocument(
          item.officialNotice?.uuid && item.officialNotice?.name ? [item.officialNotice] : [],
          './file-download',
        ),
      )
      .addRow('Total facilities', formatHistoricalNumber(item.totalFacilities))
      .addRow(isBuyout ? 'Total buy-out fee (GBP)' : 'Total refund (GBP)', formatGbp(item.invoicedBuyOutFee));

    // This deadline is for paying a buy-out; a refund is money owed back to the account.
    if (isBuyout) summary.addRow('Due date', govukDate.transform(item.dueDate));
  }

  const hasTransaction =
    transactions.length > 0 || payload.facilities.some(({ transactionCode }) => !!transactionCode?.trim());
  if (hasTransaction && payload.defaultContacts?.length) {
    // Recipients belong to historical transactions. The API supplies one common list,
    // so show it once, even when notice generation has not supplied a document yet.
    summary.addSection('Recipients').addRow(
      'Users',
      payload.defaultContacts.map(
        (contact) => `${contact.name}, ${recipientType.transform(contact.recipientType)}, ${contact.email}`,
      ),
    );
  }

  return summary.create();
}
