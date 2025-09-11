import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

export const mockTransactionDetails: BuyOutSurplusTransactionDetailsDTO = {
  id: 1,
  transactionCode: 'CCA060002',
  accountBusinessId: 'ADS_1-T00001',
  operatorName: 'Limited Company',
  targetPeriodType: 'TP6',
  targetPeriodResultType: 'BUY_OUT_REQUIRED',
  reportVersion: '2',
  submissionType: 'PRIMARY',
  fileInfoDTO: {
    name: 'CCA060002 Primary buy-out MoA.pdf',
    uuid: '19bd5e31-dd92-461c-a995-e925cc799a5b',
  },
  creationDate: '2025-04-22T17:18:19.284619Z',
  dueDate: '2025-07-01',
  paymentStatus: 'AWAITING_PAYMENT',
  chargeType: 'FEE',
  priBuyOutCarbon: '1.00000000000000000000',
  priBuyOutCost: '25.00',
  invoicedBuyOutFee: '25.00',
  invoicedSurplusGained: '0',
  invoicedPreviousPaidFees: '0.00',
  buyOutFee: '12.33',
};
