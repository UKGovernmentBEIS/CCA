import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { getByLabelText, getByRole, getByText, queryByLabelText } from '@testing';

import { FacilityPerformanceReportFiltersComponent } from './facility-performance-report-filters.component';

describe('FacilityPerformanceReportFiltersComponent', () => {
  let component: FacilityPerformanceReportFiltersComponent;
  let fixture: ComponentFixture<FacilityPerformanceReportFiltersComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: {
          reportType: 'Performance',
          targetPeriodType: 'TP8',
          targetPeriodReportType: 'INTERIM',
        },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          reportType: 'Performance',
          targetPeriodType: 'TP8',
          targetPeriodReportType: 'INTERIM',
          facilityOrTargetUnitAccountBusinessId: 'ADS-F00040',
          reportStatus: 'SUBMITTED',
          subType: 'PRIMARY',
        }),
      },
      queryParams: of({
        reportType: 'Performance',
        targetPeriodType: 'TP8',
        targetPeriodReportType: 'INTERIM',
      }),
      queryParamMap: of(
        convertToParamMap({
          reportType: 'Performance',
          targetPeriodType: 'TP8',
          targetPeriodReportType: 'INTERIM',
          facilityOrTargetUnitAccountBusinessId: 'ADS-F00040',
          reportStatus: 'SUBMITTED',
          subType: 'PRIMARY',
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [FacilityPerformanceReportFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FacilityPerformanceReportFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize filtersForm with correct query parameters', () => {
    expect(component.filtersForm.value).toEqual({
      facilityOrTargetUnitAccountBusinessId: 'ADS-F00040',
      reportStatus: 'SUBMITTED',
      subType: null,
    });
  });

  it('should render the "Filters" section', () => {
    const filtersSection = getByText('Filters');
    expect(filtersSection).toBeTruthy();
  });

  it('should have an input field for "Facility ID or TU ID"', () => {
    const facilityOrTuIdInput = getByLabelText('Facility ID or TU ID');
    expect(facilityOrTuIdInput).toBeTruthy();
  });

  it('should have interim status options', () => {
    const statusSelect = getByLabelText('Status') as HTMLSelectElement;
    const optionTexts = Array.from(statusSelect.options).map((option) => option.textContent?.trim());

    expect(optionTexts).toContain('Submitted');
    expect(optionTexts).toContain('Outstanding');
    expect(optionTexts).not.toContain('Target met');
  });

  it('should not have a select dropdown for "Subtype" for interim reports', () => {
    const subTypeSelect = queryByLabelText('Subtype');
    expect(subTypeSelect).toBeNull();
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
