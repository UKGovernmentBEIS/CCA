import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, ParamMap, provideRouter } from '@angular/router';

import { BehaviorSubject, of, Subject } from 'rxjs';

import { SchemeVersionEnum } from '@shared/types';
import { Mock } from 'vitest';

import { BuyOutAndSurplusTransactionsInfoViewService, BuyOutSurplusTransactionsListDTO } from 'cca-api';

import { mockCca3FacilityTransactionsPageOne } from '../testing/cca3-facility-mock-data';
import { mockTransactionsResponse } from '../testing/mock-data';
import { TransactionsTableComponent } from './transactions-table.component';

describe('TransactionsTableComponent', () => {
  let component: TransactionsTableComponent;
  let fixture: ComponentFixture<TransactionsTableComponent>;
  let mockService: { getBuyOutSurplusTransactions: Mock };
  let transactionsResponse: BuyOutSurplusTransactionsListDTO;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  const createComponent = () => {
    fixture = TestBed.createComponent(TransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    transactionsResponse = mockTransactionsResponse;
    queryParamMap$ = new BehaviorSubject(
      convertToParamMap({
        term: 'ABC-123',
        targetPeriodType: 'TP7',
        paymentStatus: 'PAID',
      }),
    );

    mockService = {
      getBuyOutSurplusTransactions: vi.fn().mockImplementation(() => of(transactionsResponse)),
    };

    const mockActivatedRoute = {
      queryParams: of({
        term: 'ABC-123',
        targetPeriodType: 'TP7',
        paymentStatus: 'PAID',
      }),
      queryParamMap: queryParamMap$,
      snapshot: { fragment: 'transactions' },
    };

    await TestBed.configureTestingModule({
      imports: [TransactionsTableComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: BuyOutAndSurplusTransactionsInfoViewService, useValue: mockService },
      ],
    }).compileComponents();
  });

  it('should create the component', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('keeps the same live region while loading, showing no results and showing transactions', () => {
    const emptyResponse$ = new Subject<BuyOutSurplusTransactionsListDTO>();
    const populatedResponse$ = new Subject<BuyOutSurplusTransactionsListDTO>();
    mockService.getBuyOutSurplusTransactions
      .mockReturnValueOnce(emptyResponse$)
      .mockReturnValueOnce(populatedResponse$);
    createComponent();

    const root: HTMLElement = fixture.nativeElement;
    const status = root.querySelector('[role="status"]');
    expect(status).not.toBeNull();
    expect(status.textContent.trim()).toBe('');
    expect(status.classList).toContain('govuk-visually-hidden');

    emptyResponse$.next({ transactions: [], total: 0 });
    emptyResponse$.complete();
    fixture.detectChanges();

    expect(root.querySelector('[role="status"]')).toBe(status);
    expect(status.textContent.trim()).toBe('There are no results to show.');
    expect(status.classList).not.toContain('govuk-visually-hidden');
    expect(root.querySelector('govuk-table')).toBeNull();

    queryParamMap$.next(convertToParamMap({ term: 'matching search' }));
    fixture.detectChanges();

    expect(root.querySelector('[role="status"]')).toBe(status);
    expect(status.textContent.trim()).toBe('');
    expect(status.classList).toContain('govuk-visually-hidden');

    populatedResponse$.next(mockTransactionsResponse);
    populatedResponse$.complete();
    fixture.detectChanges();

    expect(root.querySelector('[role="status"]')).toBe(status);
    expect(status.textContent.trim()).toBe('');
    expect(status.classList).toContain('govuk-visually-hidden');
    expect(root.querySelector('govuk-table')).not.toBeNull();
  });

  it('should call the service with correct parameters and update state', () => {
    createComponent();

    expect(mockService.getBuyOutSurplusTransactions).toHaveBeenCalledWith({
      term: 'ABC-123',
      targetPeriodType: 'TP7',
      paymentStatus: 'PAID',
      schemeVersion: SchemeVersionEnum.CCA_3,
      pageNumber: 0,
      pageSize: 50,
    });

    const state = component.state();
    expect(state.transactions).toEqual(mockTransactionsResponse.transactions);
    expect(state.totalItems).toBe(mockTransactionsResponse.total);
    expect(state.pageSize).toBe(50);
    expect(state.currentPage).toBe(1);
  });

  it('should link account transactions to the account details route with the fragment', () => {
    createComponent();

    expect(component['tableData']()[0].transactionLink).toEqual(['transactions', 1]);
    expect(component['tableData']()[0].transactionFragment).toBe('transaction-details');
  });

  it('should link facility transactions to the CCA3 details route without the fragment', () => {
    transactionsResponse = mockCca3FacilityTransactionsPageOne;
    createComponent();

    expect(component['tableData']()[0].transactionLink).toEqual(['cca3-transactions', 1001]);
    expect(component['tableData']()[0].transactionFragment).toBeNull();
  });

  it('should throw when a transaction has no known resource type', () => {
    transactionsResponse = { total: 1, transactions: [{ id: 1, transactionCode: 'TRX-001' }] };

    fixture = TestBed.createComponent(TransactionsTableComponent);

    expect(() => fixture.componentInstance['tableData']()).toThrow(
      'Cannot link buy-out and surplus transaction TRX-001: unknown resource type "undefined"',
    );
  });

  it('displays GBP amounts with two decimals and right-aligns the amount column', () => {
    transactionsResponse = {
      total: 4,
      transactions: ['0', '120.5', '1250', '999999999999999999.99'].map((buyOutFee, index) => ({
        id: index + 1,
        resourceType: 'FACILITY',
        paymentStatus: 'AWAITING_PAYMENT',
        buyOutFee,
      })),
    };
    createComponent();

    const root: HTMLElement = fixture.nativeElement;
    expect(root.querySelector('thead th:last-child').classList).toContain('govuk-table__header--numeric');
    const amounts = Array.from(root.querySelectorAll('tbody tr td:last-child'));
    for (const cell of amounts) expect(cell.classList).toContain('govuk-table__cell--numeric');
    expect(amounts.map((cell) => cell.textContent.trim())).toEqual([
      '0.00',
      '120.50',
      '1,250.00',
      '999,999,999,999,999,999.99',
    ]);
  });
});
