import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { getByLabelText, getByRole, getByText } from '@testing';

import { PerformanceDataReportFormProvider } from '../performance-data-report-form.provider';
import { PerformanceReportFiltersComponent } from './performance-report-filters.component';

describe('PerformanceReportFiltersComponent', () => {
  let component: PerformanceReportFiltersComponent;
  let fixture: ComponentFixture<PerformanceReportFiltersComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: { reportType: 'Performance' },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          targetUnitAccountBusinessId: '1',
          targetPeriodType: 'TP6',
          performanceOutcome: 'TARGET_MET',
          submissionType: 'PRIMARY',
        }),
      },
      queryParams: of({ reportType: 'Performance', page: '1' }),
      queryParamMap: of(convertToParamMap({ reportType: 'Performance' })),
    };

    await TestBed.configureTestingModule({
      imports: [PerformanceReportFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        PerformanceDataReportFormProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceReportFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize filtersForm with correct query parameters', () => {
    expect(component.filtersForm.value).toEqual({
      targetUnitAccountBusinessId: '1',
      targetPeriodType: 'TP6',
      performanceOutcome: 'TARGET_MET',
      submissionType: 'PRIMARY',
    });
  });

  it('should render the "Filters" section', () => {
    const filtersSection = getByText('Filters');
    expect(filtersSection).toBeTruthy();
  });

  it('should have a select dropdown for "Period"', () => {
    const periodSelect = getByLabelText('Period');
    expect(periodSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Status"', () => {
    const statusSelect = getByLabelText('Status');
    expect(statusSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Type"', () => {
    const typeSelect = getByLabelText('Type');
    expect(typeSelect).toBeTruthy();
  });

  it('should have an input field for "TU ID"', () => {
    const tuIdInput = getByLabelText('TU ID');
    expect(tuIdInput).toBeTruthy();
  });

  it('should have a submit button with "Apply"', () => {
    const applyButton = getByRole('button', { name: /Apply/i });
    expect(applyButton).toBeTruthy();
  });

  it('should have a clear button with "Clear"', () => {
    const clearButton = getByRole('button', { name: /Clear/i });
    expect(clearButton).toBeTruthy();
  });
});
