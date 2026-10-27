import { SummaryFactory } from '@shared/components';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { pendingFacilitiesCount } from '../../utils';

export function toCca3MarkAllFacilitiesSummaryData(details: FacilityBuyOutSurplusTransactionDTO) {
  const pendingFacilities = pendingFacilitiesCount(details);

  return new SummaryFactory()
    .addSection('')
    .addRow('Target unit', details.accountBusinessId)
    .addRow('Transaction ID', details.transactionCode)
    .addRow('Selected facilities', `All (${pendingFacilities} ${pendingFacilities === 1 ? 'facility' : 'facilities'})`)
    .create();
}
