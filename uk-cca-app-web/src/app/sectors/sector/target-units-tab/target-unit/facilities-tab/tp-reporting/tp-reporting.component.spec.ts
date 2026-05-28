import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { Mocked } from 'vitest';

import { RequestItemsService, RequestsService } from 'cca-api';

import { mockAvailablePeriods } from '../testing/mock-data';
import { TpReportingComponent } from './tp-reporting.component';

describe('TpReportingComponent', () => {
  let component: TpReportingComponent;
  let fixture: ComponentFixture<TpReportingComponent>;

  let mockRequestsService: Mocked<RequestsService>;
  let mockRequestItemsService: Mocked<RequestItemsService>;
  let mockRouter: Mocked<Router>;

  beforeEach(async () => {
    mockRequestsService = {
      processRequestCreateAction: vi.fn(),
    } as unknown as Mocked<RequestsService>;

    mockRequestItemsService = {
      getItemsByRequest: vi.fn(),
    } as unknown as Mocked<RequestItemsService>;

    mockRouter = {
      navigate: vi.fn(),
    } as unknown as Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [TpReportingComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { facilityId: '1' },
              data: { availablePeriods: mockAvailablePeriods },
            },
          },
        },
        { provide: RequestsService, useValue: mockRequestsService },
        { provide: RequestItemsService, useValue: mockRequestItemsService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TpReportingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.clearAllMocks();
    fixture.destroy(); // important for zoneless + signals cleanup
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialise available period types', () => {
    expect(component.availablePeriodTypes).toEqual([
      { text: 'TP7', value: 'TP7' },
      { text: 'TP8', value: 'TP8' },
    ]);
  });

  it('should compute selectedReportType based on form value (signal)', () => {
    component.form.controls.targetPeriodType.setValue('TP7');
    fixture.detectChanges();

    expect(component.selectedReportType()).toBe('FINAL');

    component.form.controls.targetPeriodType.setValue('TP8');
    fixture.detectChanges();

    expect(component.selectedReportType()).toBe('INTERIM');
  });

  it('should set hasFormErrors when form is invalid', () => {
    component.onSubmit();

    expect(component.hasFormErrors()).toBe(true);
    expect(mockRequestsService.processRequestCreateAction).not.toHaveBeenCalled();
  });

  it('should call services and navigate on successful submit', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(of({ requestId: '123' } as any));

    mockRequestItemsService.getItemsByRequest.mockReturnValue(of(null));

    component.onSubmit();

    expect(mockRequestsService.processRequestCreateAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM',
      }),
      '1',
    );

    expect(mockRequestItemsService.getItemsByRequest).toHaveBeenCalledWith('123');

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/'], { replaceUrl: true });
  });

  it('should handle TPRDF1001 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({
        error: { code: 'TPRDF1001' },
      })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('already a TPR task'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1002 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({
        error: { code: 'TPRDF1002' },
      })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('facility is not eligible'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1003 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({
        error: { code: 'TPRDF1003' },
      })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('has expired'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1004 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({
        error: { code: 'TPRDF1004' },
      })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('must be unlocked'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1005 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({
        error: { code: 'TPRDF1005' },
      })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('at least one eligible product'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });
});
