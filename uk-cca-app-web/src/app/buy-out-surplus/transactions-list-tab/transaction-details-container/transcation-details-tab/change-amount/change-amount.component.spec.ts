import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import { mockTransactionDetails } from '../testing/mock-data';
import { ChangeAmountComponent } from './change-amount.component';
import { CHANGE_AMOUNT_FORM } from './change-amount-form.provider';

describe('ChangeAmountComponent (dumb tests)', () => {
  let component: ChangeAmountComponent;
  let fixture: ComponentFixture<ChangeAmountComponent>;

  const mockUpdateService = {
    updateBuyOutSurplusTransactionAmount: jest.fn().mockReturnValue(of({})),
  };

  const mockRouter = { navigate: jest.fn() };

  const activatedRouteStub = {
    snapshot: {
      paramMap: convertToParamMap({ transactionId: mockTransactionDetails.id }),
      data: { transactionDetails: mockTransactionDetails },
    },
    // we don’t actually use the observable in these tests
    paramMap: of(convertToParamMap({ transactionId: mockTransactionDetails.id })),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeAmountComponent],
      providers: [
        { provide: CHANGE_AMOUNT_FORM, useValue: {} },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: mockRouter },
        {
          provide: BuyOutSurplusTransactionUpdateControllerService,
          useValue: mockUpdateService,
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
      .overrideComponent(ChangeAmountComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ChangeAmountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the amount label', () => {
    const label = fixture.nativeElement.querySelector('.govuk-label');
    expect(label).toBeTruthy();
    expect(label.textContent).toContain('New current amount (GBP)');
  });
});
