import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { Mocked, MockInstance } from 'vitest';

import { SectorFacilityPerformanceDataReportListDTO, SectorLevelPerformanceDataViewPagesService } from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import { mockSectorFacilitiesPerformanceReport } from '../testing/mock-data';
import { FacilityPerformanceDataReportTableComponent } from './facility-performance-data-report-table.component';

describe('FacilityPerformanceDataReportTableComponent', () => {
  let component: FacilityPerformanceDataReportTableComponent;
  let fixture: ComponentFixture<FacilityPerformanceDataReportTableComponent>;
  let mockService: Partial<Mocked<SectorLevelPerformanceDataViewPagesService>>;
  let router: Router;
  let navigateSpy: MockInstance;
  let mockExportService: Mocked<ReportingExportService>;

  const defaultQueryParams = {
    reportType: 'Performance',
    targetPeriodType: 'TP7',
    targetPeriodReportType: 'FINAL',
    facilityOrTargetUnitAccountBusinessId: 'ADS-F00040',
    reportStatus: 'TARGET_MET',
    subType: 'PRIMARY',
    page: '1',
    pageSize: '50',
  };

  async function setup(
    queryParams: Record<string, string> = defaultQueryParams,
    response: SectorFacilityPerformanceDataReportListDTO = mockSectorFacilitiesPerformanceReport,
  ) {
    mockService = {
      getSectorFacilityPerformanceDataReportList: vi.fn().mockReturnValue(of(response)),
    };

    mockExportService = {
      exportFacilityData: vi.fn(),
    } as unknown as Mocked<ReportingExportService>;

    const mockActivatedRoute = {
      snapshot: {
        queryParams,
        paramMap: convertToParamMap({ sectorId: '1' }),
        queryParamMap: convertToParamMap(queryParams),
      },
      queryParamMap: of(convertToParamMap(queryParams)),
    };

    await TestBed.configureTestingModule({
      imports: [FacilityPerformanceDataReportTableComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        Router,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SectorLevelPerformanceDataViewPagesService, useValue: mockService },
        { provide: ReportingExportService, useValue: mockExportService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = vi.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(FacilityPerformanceDataReportTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  afterEach(() => {
    navigateSpy?.mockClear();
  });

  it('should call service on init', async () => {
    await setup();

    expect(mockService.getSectorFacilityPerformanceDataReportList).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        targetPeriodType: 'TP7',
        targetPeriodReportType: 'FINAL',
        facilityOrTargetUnitAccountBusinessId: 'ADS-F00040',
        reportStatus: 'TARGET_MET',
        subType: 'PRIMARY',
        pageNumber: 0,
        pageSize: 50,
      }),
    );
  });

  it('should navigate on page change', async () => {
    await setup();

    component.onPageChange(2);

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({ page: 2 }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: 'reports',
      }),
    );
  });

  it('should not navigate if same page is selected', async () => {
    await setup();

    component.onPageChange(1);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should navigate on page size change', async () => {
    await setup();

    component.onPageSizeChange(25);

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          page: 1,
          pageSize: 25,
        }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: 'reports',
      }),
    );
  });

  it('should export the current search results', async () => {
    await setup();

    component.exportToXlsx();

    expect(mockExportService.exportFacilityData).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        targetPeriodType: 'TP7',
        targetPeriodReportType: 'FINAL',
        pageNumber: 0,
        pageSize: 3,
      }),
      3,
    );
  });

  it('should not export when there are no results', async () => {
    await setup(defaultQueryParams, { performanceDataReportItems: [], total: 0 });

    component.exportToXlsx();

    expect(mockExportService.exportFacilityData).not.toHaveBeenCalled();
  });

  it('should show subtype and locked columns for final reports', async () => {
    await setup();

    const headers = getTableHeaders();

    expect(headers).toContain('Subtype');
    expect(headers).toContain('Locked');
  });

  it('should hide subtype and locked columns for interim reports', async () => {
    await setup({
      ...defaultQueryParams,
      targetPeriodType: 'TP8',
      targetPeriodReportType: 'INTERIM',
      reportStatus: 'SUBMITTED',
      subType: 'PRIMARY',
    });

    const headers = getTableHeaders();

    expect(headers).not.toContain('Subtype');
    expect(headers).not.toContain('Locked');
    expect(mockService.getSectorFacilityPerformanceDataReportList).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        targetPeriodType: 'TP8',
        targetPeriodReportType: 'INTERIM',
        reportStatus: 'SUBMITTED',
        subType: null,
      }),
    );
  });

  it('should render a blank new variation cell when there is no new variation', async () => {
    await setup();

    const root = fixture.nativeElement as HTMLElement;
    const rows = Array.from(root.querySelectorAll('tbody tr'), (row) => row as HTMLTableRowElement);
    const secondRowCells = Array.from(rows[1].querySelectorAll('td'), (cell) => cell.textContent.trim());

    expect(secondRowCells.at(-1)).toBe('');
  });

  function getTableHeaders(): string[] {
    const root = fixture.nativeElement as HTMLElement;

    return Array.from(root.querySelectorAll('thead th'), (header) => header.textContent.trim());
  }
});
