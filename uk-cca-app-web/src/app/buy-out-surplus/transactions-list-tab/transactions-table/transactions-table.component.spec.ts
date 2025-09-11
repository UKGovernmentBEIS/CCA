import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutAndSurplusTransactionsInfoViewService } from 'cca-api';

import { mockTransactionsResponse } from '../testing/mock-data';
import { TransactionsTableComponent } from './transactions-table.component';

describe('TransactionsTableComponent', () => {
  let component: TransactionsTableComponent;
  let fixture: ComponentFixture<TransactionsTableComponent>;
  let mockService: { getBuyOutSurplusTransactions: jest.Mock };

  beforeEach(async () => {
    mockService = {
      getBuyOutSurplusTransactions: jest.fn().mockReturnValue(of(mockTransactionsResponse)),
    };

    const mockActivatedRoute = {
      queryParams: of({
        term: 'ABC-123',
        targetPeriodType: 'TP6',
        buyOutSurplusPaymentStatus: 'PAID',
      }),
      queryParamMap: of(
        convertToParamMap({
          term: 'ABC-123',
          targetPeriodType: 'TP6',
          buyOutSurplusPaymentStatus: 'PAID',
        }),
      ),
      snapshot: { fragment: 'transactions' },
    };

    await TestBed.configureTestingModule({
      imports: [TransactionsTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: BuyOutAndSurplusTransactionsInfoViewService, useValue: mockService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call the service with correct parameters and update state', () => {
    expect(mockService.getBuyOutSurplusTransactions).toHaveBeenCalledWith({
      term: 'ABC-123',
      targetPeriodType: 'TP6',
      buyOutSurplusPaymentStatus: 'PAID',
      pageNumber: 0,
      pageSize: 50,
    });

    const state = component.state();
    expect(state.transactions).toEqual(mockTransactionsResponse.transactions);
    expect(state.totalItems).toBe(mockTransactionsResponse.total);
    expect(state.pageSize).toBe(50);
    expect(state.currentPage).toBe(1);
  });
});
