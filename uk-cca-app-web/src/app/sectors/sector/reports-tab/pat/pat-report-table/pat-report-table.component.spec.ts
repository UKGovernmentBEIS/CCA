import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, ParamMap, Router } from '@angular/router';

import { BehaviorSubject, of, Subject } from 'rxjs';

import { Mocked, MockInstance } from 'vitest';

import {
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorPerformanceAccountTemplateDataReportListDTO,
} from 'cca-api';

import { ReportingExportService } from '../../services/reporting-export.service';
import { mockPatAccountsReport, mockPatFacilitiesReport } from '../testing/mock-data';
import { PatReportTableComponent } from './pat-report-table.component';

describe('PatReportTableComponent', () => {
  let component: PatReportTableComponent;
  let fixture: ComponentFixture<PatReportTableComponent>;
  let mockService: Partial<Mocked<SectorLevelPerformanceAccountTemplateDataViewPagesService>>;
  let router: Router;
  let navigateSpy: MockInstance;
  let mockExportService: Mocked<ReportingExportService>;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  const facilityQueryParams = {
    reportType: 'PAT',
    targetPeriodYear: '2026',
    term: 'ADS-F00040',
    status: 'SUBMITTED',
    page: '1',
    pageSize: '50',
  };

  const accountQueryParams = {
    ...facilityQueryParams,
    targetPeriodYear: '2024',
    term: 'ADS-T00040',
  };

  async function setup(
    queryParams: Record<string, string> = facilityQueryParams,
    response: SectorPerformanceAccountTemplateDataReportListDTO = mockPatFacilitiesReport,
  ) {
    mockService = {
      getSectorAccountPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatAccountsReport)),
      getSectorFacilityPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(response)),
    };

    mockExportService = {
      exportPatData: vi.fn(),
    } as unknown as Mocked<ReportingExportService>;

    queryParamMap$ = new BehaviorSubject(convertToParamMap(queryParams));
    const mockActivatedRoute = {
      snapshot: {
        queryParams,
        paramMap: convertToParamMap({ sectorId: '1' }),
        queryParamMap: convertToParamMap(queryParams),
      },
      queryParamMap: queryParamMap$,
    };

    await TestBed.configureTestingModule({
      imports: [PatReportTableComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        Router,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SectorLevelPerformanceAccountTemplateDataViewPagesService, useValue: mockService },
        { provide: ReportingExportService, useValue: mockExportService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = vi.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(PatReportTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  afterEach(() => {
    navigateSpy?.mockClear();
  });

  it('should call the facility endpoint for CCA3 years', async () => {
    await setup();

    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).toHaveBeenCalledWith(1, {
      term: 'ADS-F00040',
      targetPeriodYear: 2026,
      status: 'SUBMITTED',
      pageNumber: 0,
      pageSize: 50,
    });
    expect(mockService.getSectorAccountPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
  });

  it('should call the account endpoint for 2024', async () => {
    await setup(accountQueryParams);

    expect(mockService.getSectorAccountPerformanceAccountTemplateDataReportList).toHaveBeenCalledWith(1, {
      term: 'ADS-T00040',
      targetPeriodYear: 2024,
      status: 'SUBMITTED',
      pageNumber: 0,
      pageSize: 50,
    });
    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
  });

  it('should not call the service until a year is selected', async () => {
    await setup({ reportType: 'PAT' });

    expect(mockService.getSectorAccountPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
  });

  it('should not call the service for a search term below the minimum length', async () => {
    await setup({ ...facilityQueryParams, term: 'AD' });

    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
  });

  it('should show facility columns for CCA3 years', async () => {
    await setup();

    expect(getTableHeaders()).toEqual(['Facility ID', 'Facility site name', 'Date submitted', 'Status']);
  });

  it('should show target unit columns for 2024', async () => {
    await setup(accountQueryParams);

    expect(getTableHeaders()).toEqual(['Target unit ID', 'Operator', 'Date submitted', 'Status']);
  });

  it('should render outstanding rows without a submission date', async () => {
    await setup();

    const cells = getRowCells(2);

    expect(cells).toEqual(['ADS-F00042', 'Facility 42', '', 'Outstanding']);
  });

  it('should link facility rows to the facility PAT report', async () => {
    await setup();

    const link = (fixture.nativeElement as HTMLElement).querySelector('tbody tr a');

    expect(link.getAttribute('href')).toBe('/target-unit-accounts/100/facilities/40?section=pat#reports');
    const outstandingLink = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr a')[2];
    expect(outstandingLink.getAttribute('href')).toBe('/target-unit-accounts/101/facilities/42?section=pat#reports');
  });

  it('should link 2024 rows to the target unit', async () => {
    await setup(accountQueryParams, mockPatAccountsReport);

    const link = (fixture.nativeElement as HTMLElement).querySelector('tbody tr a');

    expect(link.getAttribute('href')).toBe('/target-unit-accounts/1?section=pat#reports');
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
        queryParams: expect.objectContaining({ page: 1, pageSize: 25 }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: 'reports',
      }),
    );
  });

  it('should not navigate if same page size is selected', async () => {
    await setup();

    component.onPageSizeChange(50);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should export every filtered row, not just the current page', async () => {
    await setup({ ...facilityQueryParams, page: '2', pageSize: '50' }, { ...mockPatFacilitiesReport, total: 125 });

    component.exportToCsv();

    expect(mockExportService.exportPatData).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        term: 'ADS-F00040',
        targetPeriodYear: 2026,
        status: 'SUBMITTED',
        pageNumber: 0,
        pageSize: 125,
      }),
      125,
    );
  });

  it('should not export when there are no results', async () => {
    await setup(facilityQueryParams, { items: [], total: 0 });

    component.exportToCsv();

    expect(mockExportService.exportPatData).not.toHaveBeenCalled();
  });

  it.each(['2025', '2032', 'invalid'])('should not request an unsupported year (%s)', async (targetPeriodYear) => {
    await setup({ ...facilityQueryParams, targetPeriodYear });

    expect(mockService.getSectorAccountPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).not.toHaveBeenCalled();
  });

  it('should request any supported facility year', async () => {
    await setup({ ...facilityQueryParams, targetPeriodYear: '2030' });

    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).toHaveBeenCalledWith(
      1,
      expect.objectContaining({ targetPeriodYear: 2030 }),
    );
  });

  it('should use valid defaults for invalid pagination parameters', async () => {
    await setup({ ...facilityQueryParams, page: '-1', pageSize: '2.5' });

    expect(mockService.getSectorFacilityPerformanceAccountTemplateDataReportList).toHaveBeenCalledWith(
      1,
      expect.objectContaining({ pageNumber: 0, pageSize: 50 }),
    );
  });

  it('should clear previous rows and prevent export while the next year loads', async () => {
    await setup(accountQueryParams);
    const response$ = new Subject<SectorPerformanceAccountTemplateDataReportListDTO>();
    mockService.getSectorFacilityPerformanceAccountTemplateDataReportList.mockReturnValue(response$);

    queryParamMap$.next(convertToParamMap(facilityQueryParams));
    fixture.detectChanges();
    component.exportToCsv();

    expect((fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr a')).toHaveLength(0);
    expect(mockExportService.exportPatData).not.toHaveBeenCalled();

    response$.next(mockPatFacilitiesReport);
    fixture.detectChanges();
    expect(getRowCells(0)).toEqual(['ADS-F00040', 'Facility 40', '25 Apr 2027', 'Submitted']);
  });

  it('should cancel pending requests on query changes and destruction', async () => {
    await setup(accountQueryParams);
    const response$ = new Subject<SectorPerformanceAccountTemplateDataReportListDTO>();
    mockService.getSectorFacilityPerformanceAccountTemplateDataReportList.mockReturnValue(response$);

    queryParamMap$.next(convertToParamMap(facilityQueryParams));
    fixture.detectChanges();
    expect(response$.observed).toBe(true);

    queryParamMap$.next(convertToParamMap(accountQueryParams));
    fixture.detectChanges();
    expect(response$.observed).toBe(false);

    response$.next(mockPatFacilitiesReport);
    fixture.detectChanges();
    expect(getRowCells(0)[0]).toBe(mockPatAccountsReport.items[0].businessId);

    queryParamMap$.next(convertToParamMap(facilityQueryParams));
    fixture.detectChanges();
    expect(response$.observed).toBe(true);

    fixture.destroy();
    expect(response$.observed).toBe(false);
  });

  function getTableHeaders(): string[] {
    const root = fixture.nativeElement as HTMLElement;

    return Array.from(root.querySelectorAll('thead th'), (header) => header.textContent.trim());
  }

  function getRowCells(rowIndex: number): string[] {
    const root = fixture.nativeElement as HTMLElement;
    const rows = Array.from(root.querySelectorAll('tbody tr'), (row) => row as HTMLTableRowElement);

    return Array.from(rows[rowIndex].querySelectorAll('td'), (cell) => cell.textContent.trim());
  }
});
