import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { getByLabelText, getByText, queryByRole } from '@testing';

import {
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorLevelPerformanceDataViewPagesService,
} from 'cca-api';

import { mockSectorFacilitiesPerformanceReport } from './facility-performance-data/testing/mock-data';
import { mockPatData } from './pat/testing/mock-data';
import { mockSectorAccountsPerformanceReport } from './performance-data/testing/mock-data';
import { ReportsTabComponent } from './reports-tab.component';

describe('PerformanceDataReportsComponent', () => {
  let component: ReportsTabComponent;
  let fixture: ComponentFixture<ReportsTabComponent>;

  beforeEach(async () => {
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
      queryParamMap: of(convertToParamMap({ reportType: 'Performance', targetPeriodType: 'TP6' })),
    };

    const performanceDataService = {
      getSectorAccountPerformanceDataReportList: vi.fn().mockReturnValue(of(mockSectorAccountsPerformanceReport)),
      getSectorFacilityPerformanceDataReportList: vi.fn().mockReturnValue(of(mockSectorFacilitiesPerformanceReport)),
    };

    const patDataService = {
      getSectorPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatData)),
    };

    await TestBed.configureTestingModule({
      imports: [ReportsTabComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        provideHttpClient(),
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

  it('should have a download button with "Download search results"', () => {
    const downloadButton = queryByRole('button', { name: /Download search results/i });
    expect(downloadButton).toBeTruthy();
  });
});
