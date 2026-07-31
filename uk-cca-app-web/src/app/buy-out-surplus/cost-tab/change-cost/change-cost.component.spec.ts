import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { logger } from '@shared/utils';
import { Mock } from 'vitest';

import { BuyOutAndSurplusCostInfoService, TargetPeriodBuyOutDetailsDTO } from 'cca-api';

import { ChangeCostComponent } from './change-cost.component';
import { CHANGE_COST_FORM, ChangeCostFormModel } from './change-cost-form.provider';

describe('ChangeCostComponent', () => {
  let fixture: ComponentFixture<ChangeCostComponent>;
  let form: ChangeCostFormModel;
  let mockService: { updateBuyOutCost: Mock };
  let mockRouter: { navigate: Mock };

  const tp7Details: TargetPeriodBuyOutDetailsDTO = { id: 7, businessId: 'TP7', buyOutCost: 37 };

  const activatedRouteStub = {
    snapshot: {
      paramMap: convertToParamMap({ targetPeriodType: 'TP7' }),
      data: {
        targetPeriodDetails: tp7Details,
      },
    },
  };

  beforeEach(async () => {
    mockService = {
      updateBuyOutCost: vi.fn().mockReturnValue(of(undefined)),
    };
    mockRouter = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ChangeCostComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: mockRouter },
        { provide: BuyOutAndSurplusCostInfoService, useValue: mockService },
      ],
    }).compileComponents();
  });

  function createComponent(targetPeriodDetails = tp7Details) {
    activatedRouteStub.snapshot.data.targetPeriodDetails = targetPeriodDetails;
    fixture = TestBed.createComponent(ChangeCostComponent);
    form = fixture.componentRef.injector.get(CHANGE_COST_FORM);
    fixture.detectChanges();
  }

  it('displays the target period and pre-populates its current cost', () => {
    createComponent();

    expect(fixture.nativeElement.querySelector('h1').textContent).toContain('TP7 Buyout cost per tCO2e');
    expect(fixture.nativeElement.querySelector('input').value).toBe('37');
  });

  it('leaves the cost empty when it has not yet been defined', () => {
    createComponent({ id: 9, businessId: 'TP9' });

    expect(fixture.nativeElement.querySelector('input').value).toBe('');
  });

  it.each([
    [null, 'required'],
    [0, 'invalidNatural'],
    [-1, 'invalidNatural'],
    [1.25, 'invalidNatural'],
  ])('rejects invalid cost %s', (value, expectedError) => {
    createComponent();

    form.controls.buyOutCost.setValue(value);

    expect(form.controls.buyOutCost.hasError(expectedError)).toBe(true);
  });

  it('accepts a positive integer cost', () => {
    createComponent();

    form.controls.buyOutCost.setValue(42);

    expect(form.valid).toBe(true);
  });

  it('displays the positive integer validation error', () => {
    createComponent();
    form.controls.buyOutCost.setValue(1.25);

    fixture.nativeElement.querySelector('button[type="submit"]').click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Enter a positive integer value',
    );
    expect(fixture.nativeElement.querySelector('.govuk-error-message').textContent).toContain(
      'Enter a positive integer value',
    );
  });

  it('updates the selected target period and navigates to confirmation', () => {
    createComponent();
    form.controls.buyOutCost.setValue(42);

    fixture.nativeElement.querySelector('button[type="submit"]').click();

    expect(mockService.updateBuyOutCost).toHaveBeenCalledWith('TP7', { buyOutCost: 42 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
      replaceUrl: true,
    });
  });

  it('logs update errors and does not navigate', () => {
    const error = new Error('Update failed');
    const loggerSpy = vi.spyOn(logger, 'error').mockImplementation(() => undefined);
    mockService.updateBuyOutCost.mockReturnValueOnce(throwError(() => error));
    createComponent();
    form.controls.buyOutCost.setValue(42);

    fixture.nativeElement.querySelector('button[type="submit"]').click();

    expect(loggerSpy).toHaveBeenCalledWith('Error updating buy-out cost', error);
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });
});
