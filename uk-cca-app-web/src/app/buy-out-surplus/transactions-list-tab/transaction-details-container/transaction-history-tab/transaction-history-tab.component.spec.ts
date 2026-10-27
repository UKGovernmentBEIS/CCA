import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutAndSurplusTransactionDetailInfoViewService } from 'cca-api';

import { mockTransactionDetails } from '../transaction-details-tab/testing/mock-data';
import { mockTransactionHistory } from './testing/mock-data';
import { TransactionHistoryTabComponent } from './transaction-history-tab.component';

const mockTransactionDetailInfoViewService = {
  getBuyOutSurplusTransactionDetailHistory: vi.fn().mockReturnValue(of(mockTransactionHistory)),
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
      data: { transactionDetails: mockTransactionDetails },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionHistoryTabComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: BuyOutAndSurplusTransactionDetailInfoViewService,
          useValue: mockTransactionDetailInfoViewService,
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

  it('should fetch history by transaction detail id', () => {
    expect(mockTransactionDetailInfoViewService.getBuyOutSurplusTransactionDetailHistory).toHaveBeenCalledWith(
      mockTransactionDetails.detailId,
    );
  });

  it('should show summary values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
