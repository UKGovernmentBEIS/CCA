import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, ParamMap, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { getByLabelText, getByText, queryByLabelText, queryByRole } from '@testing';

import {
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorLevelPerformanceDataViewPagesService,
} from 'cca-api';

import { mockSectorFacilitiesPerformanceReport } from './facility-performance-data/testing/mock-data';
import { mockPatAccountsReport, mockPatFacilitiesReport } from './pat/testing/mock-data';
import { mockSectorAccountsPerformanceReport } from './performance-data/testing/mock-data';
import { ReportsTabComponent } from './reports-tab.component';

describe('PerformanceDataReportsComponent', () => {
  let component: ReportsTabComponent;
  let fixture: ComponentFixture<ReportsTabComponent>;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject(convertToParamMap({ reportType: 'Performance', targetPeriodType: 'TP6' }));
    const mockActivatedRoute = {
      snapshot: {
        queryParams: { reportType: 'Performance', targetPeriodType: 'TP6' },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          reportType: 'Performance',
          targetUnitAccountBusinessId: '1',
          targetPeriodType: 'TP6',
          performanceOutcome: 'TARGET_MET',
          submissionType: 'PRIMARY',
        }),
      },
      queryParams: of({ reportType: 'Performance', targetPeriodType: 'TP6', page: '1' }),
      queryParamMap: queryParamMap$,
    };

    const performanceDataService = {
      getSectorAccountPerformanceDataReportList: vi.fn().mockReturnValue(of(mockSectorAccountsPerformanceReport)),
      getSectorFacilityPerformanceDataReportList: vi.fn().mockReturnValue(of(mockSectorFacilitiesPerformanceReport)),
    };

    const patDataService = {
      getSectorAccountPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatAccountsReport)),
      getSectorFacilityPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatFacilitiesReport)),
    };

    await TestBed.configureTestingModule({
      imports: [ReportsTabComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SectorLevelPerformanceDataViewPagesService, useValue: performanceDataService },
        { provide: SectorLevelPerformanceAccountTemplateDataViewPagesService, useValue: patDataService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly initialize sectorId', () => {
    expect(component.sectorId).toEqual(1);
  });

  it('should render the heading "Reports"', () => {
    const heading = getByText('Reports');
    expect(heading).toBeTruthy();
  });

  it('should have a select dropdown for "Report category"', () => {
    const reportTypeSelect = getByLabelText('Report category');
    expect(reportTypeSelect).toBeTruthy();
  });

  it('should have a select dropdown for "Period"', () => {
    const periodSelect = getByLabelText('Period');
    expect(periodSelect).toBeTruthy();
  });

  it('should not offer a "Year" dropdown until PAT is selected', () => {
    expect(queryByLabelText('Year')).toBeNull();
  });

  it('should offer a "Year" dropdown when PAT is selected', () => {
    component['reportTypeForm'].controls.reportType.setValue('PAT');
    fixture.detectChanges();

    const yearSelect = getByLabelText('Year') as HTMLSelectElement;
    const optionTexts = Array.from(yearSelect.options).map((option) => option.textContent?.trim());

    expect(optionTexts).toEqual(['', '2024', '2026', '2027', '2028', '2029', '2030']);
  });

  it('should not show the PAT filters or table until a year is selected', () => {
    component['reportTypeForm'].controls.reportType.setValue('PAT');
    fixture.detectChanges();

    expect(document.querySelector('cca-pat-report-filters')).toBeNull();

    component['reportTypeForm'].controls.targetPeriodYear.setValue(2026);
    fixture.detectChanges();

    expect(document.querySelector('cca-pat-report-filters')).toBeTruthy();
  });

  it('should have a download button with "Download search results"', () => {
    const downloadButton = queryByRole('button', { name: /Download search results/i });
    expect(downloadButton).toBeTruthy();
  });

  it('should restore the category and year from browser navigation without navigating again', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    queryParamMap$.next(convertToParamMap({ reportType: 'PAT', targetPeriodYear: '2026' }));
    fixture.detectChanges();
    expect(component['reportTypeForm'].value).toEqual({
      reportType: 'PAT',
      targetPeriodYear: 2026,
      targetPeriodType: null,
      targetPeriodReportType: null,
    });
    expect(document.querySelector('cca-pat-report-table')).toBeTruthy();

    queryParamMap$.next(convertToParamMap({ reportType: 'PAT' }));
    fixture.detectChanges();
    expect(component['reportTypeForm'].controls.targetPeriodYear.value).toBeNull();
    expect(document.querySelector('cca-pat-report-table')).toBeNull();
    expect(navigate).not.toHaveBeenCalled();
  });

  it('should clear PAT filters and pagination when changing year', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    queryParamMap$.next(
      convertToParamMap({
        reportType: 'PAT',
        targetPeriodYear: '2026',
        term: 'ADS-F00040',
        status: 'SUBMITTED',
        page: '3',
      }),
    );

    component['reportTypeForm'].controls.targetPeriodYear.setValue(2027);

    expect(navigate).toHaveBeenCalledWith([], {
      relativeTo: TestBed.inject(ActivatedRoute),
      queryParams: { reportType: 'PAT', targetPeriodYear: 2027, page: 1 },
      queryParamsHandling: 'replace',
      fragment: 'reports',
    });
  });

  it('should offer all PAT years and accept them in the URL', () => {
    fixture.destroy();
    queryParamMap$.next(convertToParamMap({ reportType: 'PAT', targetPeriodYear: '2030' }));
    fixture = TestBed.createComponent(ReportsTabComponent);
    fixture.detectChanges();

    const yearSelect = getByLabelText('Year') as HTMLSelectElement;
    expect(Array.from(yearSelect.options, (option) => option.textContent.trim())).toEqual([
      '',
      '2024',
      '2026',
      '2027',
      '2028',
      '2029',
      '2030',
    ]);
    expect(fixture.componentInstance['reportTypeForm'].controls.targetPeriodYear.value).toBe(2030);
    expect(document.querySelector('cca-pat-report-filters')).toBeTruthy();
    expect(document.querySelector('cca-pat-report-table')).toBeTruthy();
  });
});
