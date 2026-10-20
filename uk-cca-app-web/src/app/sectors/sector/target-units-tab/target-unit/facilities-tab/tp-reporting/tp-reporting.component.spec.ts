import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { throwError } from 'rxjs';

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

  it('should handle TPRDF1001 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({ error: { code: 'TPRDF1001' } })),
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
      throwError(() => ({ error: { code: 'TPRDF1002' } })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining(
        'target period report cannot be submitted against the target period you selected',
      ),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1003 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({ error: { code: 'TPRDF1003' } })),
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
      throwError(() => ({ error: { code: 'TPRDF1004' } })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining('this target period must be unlocked before submitting'),
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1005 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({ error: { code: 'TPRDF1005' } })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError:
        'The baseline data for this facility must contain at least one product with a base year equal to the facility base year, and at least one product with a base year less than or equal to the year the report data relates to. You must submit a variation to correct the base year of products in this facility before you can submit your report.',
    });

    expect(component.hasFormErrors()).toBe(true);
  });

  it('should handle TPRDF1009 error', () => {
    component.form.controls.targetPeriodType.setValue('TP7');

    mockRequestsService.processRequestCreateAction.mockReturnValue(
      throwError(() => ({ error: { code: 'TPRDF1009' } })),
    );

    component.onSubmit();

    expect(component.form.controls.targetPeriodType.errors).toEqual({
      responseError: expect.stringContaining(`error in the facility’s baseline data`),
    });

    expect(component.hasFormErrors()).toBe(true);
  });
});
