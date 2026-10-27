import { computed, inject, Service, signal } from '@angular/core';

import { ErrorSummaryInfo, SummaryFactory } from '@shared/components';
import { formatGbp } from '@shared/utils';

import {
  FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { CCA3_CHANGE_AMOUNT_FORM } from './cca3-change-amount-form.provider';

export const TERMINATED_AMOUNT_MESSAGE = 'You cannot update the amount for a facility with a terminated payment status';
export const BATCH_AMOUNT_MESSAGE = 'You cannot make an adjustment while a Batch Run is in progress.';

// Shared by the form, review and guard; the flow container clears the draft on exit.
@Service({ autoProvided: false })
export class Cca3ChangeAmountState {
  readonly form = inject(CCA3_CHANGE_AMOUNT_FORM);
  readonly errorSummary = signal<ErrorSummaryInfo | null>(null);
  readonly terminated = signal(false);
  readonly transaction = signal<FacilityBuyOutSurplusTransactionDTO>(null);
  readonly facility = signal<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO>(null);
  readonly returnUrl = computed(() => `/buyout-surplus/cca3-transactions/${this.transaction().id}`);
  readonly returnText = computed(
    () =>
      `Return to: ${this.transaction().chargeType === 'FEE' ? 'Buy-out' : 'Refund'} MoA ${this.transaction().transactionCode}`,
  );
  readonly evidenceDownloadUrl = computed(
    () =>
      `${this.returnUrl()}/facilities/${this.facility().facilityBusinessId}/file-evidences-download/${this.facility().id}/attachment`,
  );
  readonly summaryData = computed(() =>
    new SummaryFactory()
      .addSection('')
      .addRow('Transaction ID', this.transaction().transactionCode)
      .addRow('Facility ID', this.facility().facilityBusinessId)
      .addRow('Initial amount (GBP)', formatGbp(this.facility().invoicedBuyFee))
      .addRow('Current amount (GBP)', formatGbp(this.facility().buyOutFee))
      .create(),
  );

  initialize(
    transaction: FacilityBuyOutSurplusTransactionDTO,
    facility: FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO,
  ) {
    this.transaction.set(transaction);
    this.facility.set(facility);
    this.resetDraft();
    if (facility.paymentStatus === 'TERMINATED') this.showError(TERMINATED_AMOUNT_MESSAGE, true);
  }

  resetDraft() {
    this.form.reset({ amount: '', comments: '', evidenceFiles: [] });
    this.errorSummary.set(null);
    this.terminated.set(false);
  }

  showError(message: string, terminated = false) {
    this.errorSummary.set({ message, link: this.returnUrl(), linkText: this.returnText() });
    if (terminated) this.terminated.set(true);
  }
}
