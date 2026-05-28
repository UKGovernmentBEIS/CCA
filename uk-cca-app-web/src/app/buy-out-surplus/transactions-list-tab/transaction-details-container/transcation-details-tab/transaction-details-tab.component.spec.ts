import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { Mock } from 'vitest';

import { BuyOutAndSurplusTransactionsInfoViewService } from 'cca-api';

import { mockTransactionDetails } from './testing/mock-data';
import { TransactionDetailsTabComponent } from './transaction-details-tab.component';

describe('TransactionDetailsTabComponent', () => {
  let component: TransactionDetailsTabComponent;
  let fixture: ComponentFixture<TransactionDetailsTabComponent>;
  let mockService: { getBuyOutSurplusTransactionDetails: Mock };

  const activatedRouteStub = {
    paramMap: of({
      transactionId: 1,
    }),
    snapshot: {
      paramMap: convertToParamMap({
        transactionId: 1,
      }),
      data: {
        transactionDetails: mockTransactionDetails,
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionDetailsTabComponent],
      providers: [
        { provide: BuyOutAndSurplusTransactionsInfoViewService, useValue: mockService },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionDetailsTabComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
