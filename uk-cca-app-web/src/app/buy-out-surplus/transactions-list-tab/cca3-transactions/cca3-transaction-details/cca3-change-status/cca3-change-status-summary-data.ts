import { SummaryFactory } from '@shared/components';

export function toCca3ChangeStatusSummaryData(transactionCode: string, selectedFacilitiesCount: number) {
  return new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', transactionCode)
    .addRow('Selected facilities', `${selectedFacilitiesCount}`)
    .create();
}
