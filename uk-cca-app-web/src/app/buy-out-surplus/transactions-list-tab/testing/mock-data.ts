import { BuyOutSurplusTransactionsListDTO } from 'cca-api';

export const mockTransactionsResponse: BuyOutSurplusTransactionsListDTO = {
  transactions: [
    {
      id: 1,
      accountBusinessId: 'ABC-123',
      operatorName: 'Test Operator',
      transactionCode: 'TRX-001',
      creationDate: '2024-01-01T00:00:00Z',
      paymentStatus: 'PAID',
      buyOutFee: '',
    },
  ],
  total: 1,
};
