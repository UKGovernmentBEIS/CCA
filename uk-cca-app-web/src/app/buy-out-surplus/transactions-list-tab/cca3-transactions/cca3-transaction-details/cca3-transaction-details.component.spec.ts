import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { BuyOutAndSurplusFacilityTransactionViewService, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import {
  mockFacilityTransactionDetailsPageOne,
  mockFacilityTransactionFee,
  mockFacilityTransactionRefund,
} from '../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsComponent } from './cca3-transaction-details.component';
import { Cca3TransactionDetailsStore } from './cca3-transaction-details.store';

describe('Cca3TransactionDetailsComponent', () => {
  const mockService = {
    getFacilityBuyOutSurplusTransactionDetails: vi.fn().mockReturnValue(of(mockFacilityTransactionDetailsPageOne)),
  };

  // Mirrors cca3-transactions.routes.ts: the parent route resolves the transaction, the child renders it.
  async function render(transactionDetails: FacilityBuyOutSurplusTransactionDTO) {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: 'transactions/:transactionId',
              resolve: { transactionDetails: () => transactionDetails },
              children: [{ path: '', component: Cca3TransactionDetailsComponent }],
            },
          ],
          withComponentInputBinding(),
        ),
        Cca3TransactionDetailsStore,
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: mockService },
      ],
    });

    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/transactions/1001?page=1', Cca3TransactionDetailsComponent);
    return harness.routeNativeElement.textContent;
  }

  it('should render the transaction heading and the buy-out details from the parent resolver', async () => {
    const content = await render(mockFacilityTransactionFee);

    expect(content).toContain(mockFacilityTransactionFee.transactionCode);
    expect(content).toContain('Buy-out MoA file');
    expect(content).toContain('Buy-out fee reconciliation');
    expect(content).toContain('Expected amount (GBP)');
  });

  it('should render the refund details for a refund transaction', async () => {
    const content = await render(mockFacilityTransactionRefund);

    expect(content).toContain(mockFacilityTransactionRefund.transactionCode);
    expect(content).toContain('Refund reconciliation');
    expect(content).toContain('Refund letter');
    expect(content).not.toContain('Buy-out fee reconciliation');
  });
});
