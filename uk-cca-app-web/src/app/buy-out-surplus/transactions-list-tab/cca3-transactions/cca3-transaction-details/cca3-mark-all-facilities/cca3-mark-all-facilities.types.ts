import { BuyOutSurplusTransactionMarkPaymentStatusDTO, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

export type Cca3MarkAllStatus = Extract<BuyOutSurplusTransactionMarkPaymentStatusDTO['status'], 'PAID' | 'REFUNDED'>;

export type Cca3MarkAllContent = {
  chargeType: FacilityBuyOutSurplusTransactionDTO['chargeType'];
  heading: string;
  intro: string;
  eligibilityCriteria: string;
  confirmation: string;
};

export const CCA3_MARK_ALL_CONTENT: Record<Cca3MarkAllStatus, Cca3MarkAllContent> = {
  PAID: {
    chargeType: 'FEE',
    heading: 'Are you sure you want to mark all selected facilities as paid?',
    intro: "Only facilities that meet the 'awaiting payment' eligibility criteria when processed will be updated.",
    eligibilityCriteria: "'awaiting payment'",
    confirmation: 'All facilities have been marked as paid',
  },
  REFUNDED: {
    chargeType: 'REFUND',
    heading: 'Are you sure you want to mark all awaiting facilities as refunded?',
    intro:
      "Only facilities that meet the 'awaiting refund' eligibility criteria when processed will be updated. Facilities being terminated or no longer eligible will not be updated.",
    eligibilityCriteria: "'awaiting refund'",
    confirmation: 'All facilities have been marked as refunded',
  },
};
