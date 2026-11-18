import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { TransactionDetailsContainerComponent } from './transaction-details-container.component';
import { mockTransactionDetails } from './transcation-details-tab/testing/mock-data';

describe('TransactionDetailsContainerComponent', () => {
  let component: TransactionDetailsContainerComponent;
  let fixture: ComponentFixture<TransactionDetailsContainerComponent>;

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
    fragment: of('transaction-details'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionDetailsContainerComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionDetailsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
