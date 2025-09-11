import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutAndSurplusTransactionInfoViewService } from 'cca-api';

import { mockTransactionHistory } from './testing/mock-data';
import { TransactionHistoryTabComponent } from './transaction-history-tab.component';

const mockGetBuyOutSurplusTransactionHistoryService = {
  getBuyOutSurplusTransactionHistory: jest.fn().mockReturnValue(of(mockTransactionHistory)),
};

describe('TransactionHistoryTabComponent', () => {
  let component: TransactionHistoryTabComponent;
  let fixture: ComponentFixture<TransactionHistoryTabComponent>;

  const activatedRouteStub = {
    paramMap: of({
      transactionId: 1,
    }),
    snapshot: {
      paramMap: convertToParamMap({
        transactionId: 1,
      }),
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionHistoryTabComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: BuyOutAndSurplusTransactionInfoViewService,
          useValue: mockGetBuyOutSurplusTransactionHistoryService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionHistoryTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
