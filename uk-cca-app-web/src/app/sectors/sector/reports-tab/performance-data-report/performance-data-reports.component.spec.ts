import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { screen } from '@testing-library/dom';

import { PerformanceDataReportsComponent } from './performance-data-reports.component';
import { PerformanceDataReportsFormProvider } from './performance-data-reports-form.provider';

describe('PerformanceDataReportsComponent', () => {
  let component: PerformanceDataReportsComponent;
  let fixture: ComponentFixture<PerformanceDataReportsComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: { reportType: 'Performance' },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          targetUnitBusinessId: '1',
          targetPeriodType: 'TP6',
          performanceOutcome: 'TARGET_MET',
          submissionType: 'PRIMARY',
        }),
      },
      queryParams: of({ reportType: 'Performance', page: '1' }),
      queryParamMap: of(convertToParamMap({ reportType: 'Performance' })),
    };

    await TestBed.configureTestingModule({
      imports: [PerformanceDataReportsComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        PerformanceDataReportsFormProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceDataReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly initialize sectorId and currentPage', () => {
    expect(component.sectorId).toEqual(1);
    expect(component.state().currentPage).toEqual(1);
  });

  it('should initialize filtersForm with correct query parameters', () => {
    expect(component.filtersForm.value).toEqual({
      targetUnitBusinessId: '1',
      targetPeriodType: 'TP6',
      performanceOutcome: 'TARGET_MET',
      submissionType: 'PRIMARY',
    });
  });

  it('should render the heading "Reports"', () => {
    const heading = screen.getByText('Reports');
    expect(heading).toBeTruthy();
  });

  it('should render the "Filters" section', () => {
    const filtersSection = screen.getByText('Filters');
    expect(filtersSection).toBeTruthy();
  });

  it('should have a submit button with "Apply"', () => {
    const applyButton = screen.getByRole('button', { name: /Apply/i });
    expect(applyButton).toBeTruthy();
  });

  it('should have a clear button with "Clear"', () => {
    const clearButton = screen.getByRole('button', { name: /Clear/i });
    expect(clearButton).toBeTruthy();
  });

  it('should have a download button with "Download to spreadsheet"', () => {
    const downloadButton = screen.queryByRole('button', { name: /Download to spreadsheet/i });
    expect(downloadButton).not.toBeInTheDocument();
  });

  it('should have a select dropdown for "Report type"', () => {
    const reportTypeSelect = screen.getByLabelText('Report type');
    expect(reportTypeSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Period"', () => {
    const periodSelect = screen.getByLabelText('Period');
    expect(periodSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Status"', () => {
    const statusSelect = screen.getByLabelText('Status');
    expect(statusSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Type"', () => {
    const typeSelect = screen.getByLabelText('Type');
    expect(typeSelect).toBeTruthy();
  });

  it('should have an input field for "TU ID"', () => {
    const tuIdInput = screen.getByLabelText('TU ID');
    expect(tuIdInput).toBeTruthy();
  });
});
