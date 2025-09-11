import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import { mockTransactionDetails } from '../testing/mock-data';
import { ChangeStatusComponent } from './change-status.component';
import { CHANGE_STATUS_FORM } from './change-status-form.provider';

const mockUpdateService = {
  updateBuyOutSurplusTransactionPaymentStatus: jest.fn().mockReturnValue(of({})),
};

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

describe('ChangeStatusComponent', () => {
  let component: ChangeStatusComponent;
  let fixture: ComponentFixture<ChangeStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: CHANGE_STATUS_FORM, useValue: {} },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: BuyOutSurplusTransactionUpdateControllerService, useValue: mockUpdateService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
      imports: [ChangeStatusComponent],
    })
      .overrideComponent(ChangeStatusComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ChangeStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set transactionId from route snapshot', () => {
    expect(component.transactionId).toBe(mockTransactionDetails.id);
  });

  it('should initialize form status with current paymentStatus', () => {
    expect(component.form.value.status).toBe(mockTransactionDetails.paymentStatus);
  });

  it('should map comments correctly for UNDER_APPEAL status', () => {
    component.form.patchValue({
      status: 'UNDER_APPEAL',
      appealComments: 'Appeal reason',
      evidenceFiles: [],
    });

    component.onSubmit();

    expect(mockUpdateService.updateBuyOutSurplusTransactionPaymentStatus).toHaveBeenCalledWith(
      mockTransactionDetails.id,
      expect.objectContaining({
        status: 'UNDER_APPEAL',
        comments: 'Appeal reason',
      }),
    );
  });
});
