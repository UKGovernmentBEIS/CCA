import { HttpErrorResponse, provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { AccountBuyOutSurplusTransactionDTO, BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import { mockTransactionDetails } from '../testing/mock-data';
import { BATCH_RUN_IN_PROGRESS_MESSAGE, ChangeStatusComponent } from './change-status.component';
import { CHANGE_STATUS_FORM } from './change-status-form.provider';

const mockUpdateService = {
  updateBuyOutSurplusTransactionDetailsPaymentStatus: vi.fn(),
};

describe('ChangeStatusComponent', () => {
  let component: ChangeStatusComponent;
  let fixture: ComponentFixture<ChangeStatusComponent>;
  let router: Router;

  async function setup(transactionDetails: AccountBuyOutSurplusTransactionDTO = mockTransactionDetails) {
    await TestBed.configureTestingModule({
      providers: [
        { provide: CHANGE_STATUS_FORM, useValue: {} },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ transactionId: transactionDetails.id }),
              data: { transactionDetails },
            },
          },
        },
        { provide: BuyOutSurplusTransactionUpdateControllerService, useValue: mockUpdateService },
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
      ],
      imports: [ChangeStatusComponent],
    })
      .overrideComponent(ChangeStatusComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Eager },
      })
      .compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(ChangeStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  function radioLabels(): string[] {
    return Array.from(fixture.nativeElement.querySelectorAll('.govuk-radios__label') as NodeListOf<HTMLElement>).map(
      (label) => label.textContent.trim(),
    );
  }

  function submitUnderAppeal() {
    component.form.patchValue({
      status: 'UNDER_APPEAL',
      dateOfAppeal: new Date('2026-01-15') as unknown as string,
      appealComments: 'Appeal reason',
    });

    component.onSubmit();
    fixture.detectChanges();
  }

  beforeEach(() => {
    mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mockReset().mockReturnValue(of({}));
  });

  describe('for a buy-out fee transaction', () => {
    beforeEach(() => setup());

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should set transactionId from route snapshot', () => {
      expect(component.transactionId).toBe(mockTransactionDetails.id);
    });

    it('should initialize form status with current paymentStatus', () => {
      expect(component.form.value.status).toBe(mockTransactionDetails.paymentStatus);
    });

    it('should show the buy-out fee status options', () => {
      expect(radioLabels()).toEqual(['Awaiting payment', 'Under appeal', 'Not required', 'Paid']);
    });

    it('should show the Confirm button', () => {
      expect(fixture.nativeElement.querySelector('button[type="submit"]').textContent.trim()).toBe('Confirm');
    });

    it('should require a comment and a date of appeal for UNDER_APPEAL status', () => {
      component.form.patchValue({ status: 'UNDER_APPEAL' });
      fixture.detectChanges();

      expect(component.form.controls.appealComments.errors).toEqual({ required: 'Please enter a comment' });
      expect(component.form.controls.dateOfAppeal.errors).toEqual({ required: 'Please enter a date' });
    });

    it('should map comments and date of appeal correctly for UNDER_APPEAL status', () => {
      submitUnderAppeal();

      expect(mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus).toHaveBeenCalledWith(
        mockTransactionDetails.id,
        expect.objectContaining({
          status: 'UNDER_APPEAL',
          dateOfAppeal: new Date('2026-01-15'),
          comments: 'Appeal reason',
          detailIds: [mockTransactionDetails.detailId],
        }),
      );
      expect(router.navigate).toHaveBeenCalledWith(['confirmation'], expect.anything());
    });

    it('should send the payment date and not the date of appeal for PAID status', () => {
      const paymentDate = new Date('2026-02-01');

      component.form.patchValue({
        status: 'UNDER_APPEAL',
        dateOfAppeal: new Date('2026-01-15') as unknown as string,
      });
      component.form.patchValue({ status: 'PAID', paymentDate: paymentDate as unknown as string });
      component.onSubmit();

      const payload = mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mock.calls[0][1];
      expect(payload).toEqual(expect.objectContaining({ status: 'PAID', paymentDate }));
      expect(payload.dateOfAppeal).toBeUndefined();
    });

    it('should show an error when a batch run is in progress', () => {
      mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1004' } })),
      );

      submitUnderAppeal();

      expect(fixture.nativeElement.querySelector('cca-error-summary').textContent).toContain(
        BATCH_RUN_IN_PROGRESS_MESSAGE,
      );
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should clear the batch run error when resubmitting', () => {
      mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValueOnce(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1004' } })),
      );

      submitUnderAppeal();
      expect(fixture.nativeElement.querySelector('cca-error-summary')).not.toBeNull();

      component.onSubmit();
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('cca-error-summary')).toBeNull();
      expect(router.navigate).toHaveBeenCalledWith(['confirmation'], expect.anything());
    });

    it('should reject comments longer than 10000 characters', () => {
      component.form.patchValue({ awaitingPaymentComments: 'a'.repeat(10001) });

      expect(component.form.controls.awaitingPaymentComments.errors).toEqual({
        maxlength: 'The comments should not be more than 10000 characters',
      });
    });
  });

  describe('for an overpayment transaction', () => {
    beforeEach(() => setup({ ...mockTransactionDetails, chargeType: 'REFUND', paymentStatus: 'AWAITING_REFUND' }));

    it('should show the overpayment status options', () => {
      expect(radioLabels()).toEqual(['Awaiting refund', 'Not required', 'Refunded']);
    });

    it('should disable the fee only controls', () => {
      const { paymentDate, dateOfAppeal, appealComments } = component.form.controls;

      expect([paymentDate.disabled, dateOfAppeal.disabled, appealComments.disabled]).toEqual([true, true, true]);
      expect(component.form.valid).toBe(true);
    });
  });
});
