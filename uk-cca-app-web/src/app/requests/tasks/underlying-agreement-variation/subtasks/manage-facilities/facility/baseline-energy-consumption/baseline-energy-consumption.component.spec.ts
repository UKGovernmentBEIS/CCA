import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import {
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';

import { BaselineEnergyConsumptionComponent } from './baseline-energy-consumption.component';
import { FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM } from './baseline-energy-consumption-form.provider';
import {
  mockActivatedRoute,
  mockBaselineEnergyConsumption,
  mockRequestTaskId,
  mockRequestTaskPayload,
  mockUnderlyingAgreement,
} from './testing/mock-data';

describe('BaselineEnergyConsumptionComponent', () => {
  let component: BaselineEnergyConsumptionComponent;
  let fixture: ComponentFixture<BaselineEnergyConsumptionComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPayload)),
    };

    const formBuilder = new FormBuilder();

    const testForm = formBuilder.group(
      {
        totalFixedEnergy: [100],
        hasVariableEnergy: [true],
        baselineVariableEnergy: [200],
        totalThroughput: [50],
        throughputUnit: ['tonnes'],
        variableEnergyType: ['TOTALS'],
        products: [[]],
      },
      {
        updateOn: 'submit',
      },
    );

    TestBed.configureTestingModule({
      imports: [BaselineEnergyConsumptionComponent, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM, useValue: testForm },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal(mockRequestTaskPayload);
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(mockRequestTaskId);
      if (selector === underlyingAgreementQuery.selectUnderlyingAgreement) {
        return signal(mockUnderlyingAgreement);
      }

      if (selector === underlyingAgreementReviewQuery.selectReviewSectionsCompleted) {
        return signal({});
      }

      if (selector === underlyingAgreementVariationQuery.selectReviewGroupDecisions) {
        return signal({});
      }

      if (selector === underlyingAgreementVariationQuery.selectFacilityReviewGroupDecisions) {
        return signal({});
      }

      if (typeof selector === 'function') {
        return signal({
          measurementType: 'ENERGY_KWH',
          baselineDate: '2022-01-01',
          ...mockBaselineEnergyConsumption,
        });
      }

      return signal({});
    });

    fixture = TestBed.createComponent(BaselineEnergyConsumptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the form with the correct initial values', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should have the correct form controls', () => {
    expect(component['form'].contains('totalFixedEnergy')).toBe(true);
    expect(component['form'].contains('hasVariableEnergy')).toBe(true);
    expect(component['form'].contains('baselineVariableEnergy')).toBe(true);
    expect(component['form'].contains('totalThroughput')).toBe(true);
    expect(component['form'].contains('throughputUnit')).toBe(true);
    expect(component['form'].contains('variableEnergyType')).toBe(true);
    expect(component['form'].contains('products')).toBe(true);
  });

  it('should display caption text', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Baseline energy');
  });

  it('should have fixed energy value in form', () => {
    expect(component['form'].get('totalFixedEnergy')?.value).toBe('100');
  });

  it('should have variable energy flag set to true', () => {
    expect(component['form'].get('hasVariableEnergy')?.value).toBe(true);
  });

  it('should have variable energy type set to TOTALS', () => {
    expect(component['form'].get('variableEnergyType')?.value).toBe('TOTALS');
  });

  it('should call saveRequestTaskAction on form submit', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    component.onSubmit();
    expect(onSubmitSpy).toHaveBeenCalled();
  });

  it('should calculate fixed energy correctly', () => {
    expect(component['calculatedFixedEnergy']()).toBe('100');
  });

  it('should show variable energy type field when hasVariableEnergy is true', () => {
    expect(component['showVariableEnergyType']()).toBe(true);
  });

  it('should show totals only fields when variableEnergyType is TOTALS', () => {
    expect(component['showTotalsOnlyFields']()).toBe(true);
  });

  it('should not show split by product fields when variableEnergyType is TOTALS', () => {
    expect(component['showSplitByProductFields']()).toBe(false);
  });

  it('should show throughput fields when variableEnergyType is TOTALS', () => {
    expect(component['showThroughputFields']()).toBe(true);
  });

  it('should show throughput fields when hasVariableEnergy is false', () => {
    component['form'].patchValue({ hasVariableEnergy: false });
    fixture.detectChanges();
    expect(component['showThroughputFields']()).toBe(true);
  });

  it('should not show throughput fields when variableEnergyType is BY_PRODUCT', () => {
    component['form'].patchValue({ hasVariableEnergy: true, variableEnergyType: 'BY_PRODUCT' });
    fixture.detectChanges();
    expect(component['showThroughputFields']()).toBe(false);
  });

  it('should have baseline year property', () => {
    expect(component['baselineYear']).toBeDefined();
  });
});
