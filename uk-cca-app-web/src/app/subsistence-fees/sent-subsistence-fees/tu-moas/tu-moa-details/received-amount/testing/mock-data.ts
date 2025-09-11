import { ReceivedAmountState } from '../received-amount.store';

export const mockReceivedAmountStoreState: ReceivedAmountState = {
  transactionId: 'CCATM01206',
  businessId: 'ADS_52',
  name: 'Aerospace_52',
  changeType: 'add',
  details: { transactionAmount: '+350', comments: 'mplah mplah', evidenceFiles: {} },
  currentTotalAmount: '370',
  receivedAmount: '5100',
  receivedAmountHistoryList: [
    {
      id: 1,
      submitter: 'Regulator England',
      submissionDate: '2025-04-14T11:30:46.567207Z',
      transactionAmount: '20',
      comments: 'asdasdasdsad',
      evidenceFiles: {
        'cee29692-0b9f-4b46-b022-e1e443490f6e': 'sample_profile1.png',
        'd6ab5f42-c75c-4e7e-a61e-5aca52ff78c7': 'sample_profile.bmp',
      },
    },
    {
      id: 2,
      submitter: 'Regulator England',
      submissionDate: '2025-04-14T11:30:39.70945Z',
      transactionAmount: '20',
      comments: 'asdasdasdsad',
      evidenceFiles: {},
    },
  ],
};
