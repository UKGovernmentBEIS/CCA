import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { SectorLevelPerformanceAccountTemplateDataViewPagesService } from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import { mockPatData } from '../testing/mock-data';
import { PatReportTableComponent } from './pat-report-table.component';

describe('PatReportTableComponent', () => {
  let component: PatReportTableComponent;
  let fixture: ComponentFixture<PatReportTableComponent>;
  let mockService: Partial<jest.Mocked<SectorLevelPerformanceAccountTemplateDataViewPagesService>>;
  let router: Router;
  let navigateSpy: jest.SpyInstance;
  let mockExportService: jest.Mocked<ReportingExportService>;

  beforeEach(async () => {
    mockService = {
      getSectorPerformanceAccountTemplateDataReportList: jest.fn().mockReturnValue(of(mockPatData)),
    };

    mockExportService = {
      exportPatData: jest.fn(),
    } as unknown as jest.Mocked<ReportingExportService>;

    const mockActivatedRoute = {
      snapshot: {
        queryParams: {
          reportType: 'PAT',
          targetUnitAccountBusinessId: 'ADS-T0001',
          targetPeriodType: 'TP6',
          status: 'SUBMITTED',
          submissionType: 'FINAL',
          page: '1',
          pageSize: '50',
        },
        paramMap: convertToParamMap({ sectorId: '1' }),
        queryParamMap: convertToParamMap({
          reportType: 'PAT',
          targetUnitAccountBusinessId: 'ADS-T0001',
          targetPeriodType: 'TP6',
          status: 'SUBMITTED',
          submissionType: 'FINAL',
        }),
      },
      queryParamMap: of(
        convertToParamMap({
          reportType: 'PAT',
          targetUnitAccountBusinessId: 'ADS-T0001',
          targetPeriodType: 'TP6',
          status: 'SUBMITTED',
          submissionType: 'FINAL',
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [PatReportTableComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        Router,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SectorLevelPerformanceAccountTemplateDataViewPagesService, useValue: mockService },
        { provide: ReportingExportService, useValue: mockExportService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(PatReportTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    navigateSpy.mockClear();
  });

  it('should handle page change correctly', () => {
    component.onPageChange(2);

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          page: 2,
        }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: 'reports',
      }),
    );
  });

  it('should not navigate if same page is selected', () => {
    component.onPageChange(1);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should handle page size change correctly', () => {
    component.onPageSizeChange(25);

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          pageSize: 25,
        }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: 'reports',
      }),
    );
  });

  it('should not navigate if same page size is selected', () => {
    component.onPageSizeChange(50);
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
