import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import {
  mockFacilityTransactionDetailsPageOne,
  mockFacilityTransactionFee,
  mockFacilityTransactionRefund,
} from '../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsComponent } from './cca3-transaction-details.component';
import { Cca3TransactionDetailsStore } from './cca3-transaction-details.store';

describe('Cca3TransactionDetailsComponent', () => {
  let component: Cca3TransactionDetailsComponent;
  let fixture: ComponentFixture<Cca3TransactionDetailsComponent>;
  let activatedRoute: ActivatedRouteStub;

  const resolvedData = { transactionDetails: mockFacilityTransactionFee };

  const mockService = {
    getFacilityBuyOutSurplusTransactionDetails: vi.fn().mockReturnValue(of(mockFacilityTransactionDetailsPageOne)),
  };

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub({ transactionId: '1001' }, { page: '1' }, resolvedData);

    await TestBed.configureTestingModule({
      imports: [Cca3TransactionDetailsComponent],
      providers: [
        provideRouter([]),
        Cca3TransactionDetailsStore,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: mockService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Cca3TransactionDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the transaction heading and the buy-out details', () => {
    const content = fixture.nativeElement.textContent;

    expect(content).toContain(mockFacilityTransactionFee.transactionCode);
    expect(content).toContain('Buy-out MoA file');
    expect(content).toContain('Buy-out fee reconciliation');
    expect(content).toContain('Expected amount (GBP)');
  });

  it('should render the refund details for a refund transaction', async () => {
    resolvedData.transactionDetails = mockFacilityTransactionRefund;

    fixture = TestBed.createComponent(Cca3TransactionDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();

    const content = fixture.nativeElement.textContent;

    expect(content).toContain(mockFacilityTransactionRefund.transactionCode);
    expect(content).toContain('Refund reconciliation');
    expect(content).toContain('Refund letter');
    expect(content).not.toContain('Buy-out fee reconciliation');
  });
});
