import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { screen } from '@testing-library/dom';

import { PerformanceDataReportFormProvider } from '../../performance-data/performance-data-report-form.provider';
import { PatReportFiltersComponent } from './pat-report-filters.component';

describe('PatReportFiltersComponent', () => {
  let component: PatReportFiltersComponent;
  let fixture: ComponentFixture<PatReportFiltersComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: { reportType: 'PAT' },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          targetUnitAccountBusinessId: '1',
          targetPeriodType: 'TP6',
          status: 'SUBMITTED',
          submissionType: 'FINAL',
        }),
      },
      queryParams: of({
        reportType: 'PAT',
        targetUnitAccountBusinessId: '1',
        targetPeriodType: 'TP6',
        status: 'SUBMITTED',
        submissionType: 'FINAL',
      }),
      queryParamMap: of(convertToParamMap({ reportType: 'PAT' })),
    };

    await TestBed.configureTestingModule({
      imports: [PatReportFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        PerformanceDataReportFormProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PatReportFiltersComponent);
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
      status: 'SUBMITTED',
      submissionType: 'FINAL',
    });
  });

  it('should render the "Filters" section', () => {
    const filtersSection = screen.getByText('Filters');
    expect(filtersSection).toBeTruthy();
  });

  it('should have a select dropdown for "Status"', () => {
    const statusSelect = screen.getByLabelText('Status');
    expect(statusSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Period"', () => {
    const periodSelect = screen.getByLabelText('Period');
    expect(periodSelect).toBeTruthy();
  });

  it('should have an input field for "TU ID"', () => {
    const tuIdInput = screen.getByLabelText('TU ID');
    expect(tuIdInput).toBeTruthy();
  });

  it('should have a select dropdown for "Type"', () => {
    const typeSelect = screen.getByLabelText('Type');
    expect(typeSelect).toBeTruthy();
  });

  it('should have a submit button with "Apply"', () => {
    const applyButton = screen.getByRole('button', { name: /Apply/i });
    expect(applyButton).toBeTruthy();
  });

  it('should have a clear button with "Clear"', () => {
    const clearButton = screen.getByRole('button', { name: /Clear/i });
    expect(clearButton).toBeTruthy();
  });
});
