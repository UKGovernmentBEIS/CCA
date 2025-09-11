import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { SectorLevelPerformanceDataViewPagesService } from 'cca-api';

import { mockSectorAccountsPerformanceReport } from '../testing/mock-data';
import { PerformanceDataReportTableComponent } from './performance-data-report-table.component';

describe('PerformanceDataReportTableComponent', () => {
  let component: PerformanceDataReportTableComponent;
  let mockService: Partial<jest.Mocked<SectorLevelPerformanceDataViewPagesService>>;
  let router: Router;
  let navigateSpy: jest.SpyInstance;

  beforeEach(async () => {
    mockService = {
      getSectorAccountPerformanceDataReportList: jest.fn().mockReturnValue(of(mockSectorAccountsPerformanceReport)),
    };

    await TestBed.configureTestingModule({
      imports: [PerformanceDataReportTableComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        Router,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ sectorId: '1' }),
              queryParamMap: convertToParamMap({
                reportType: 'Performance',
                targetUnitAccountBusinessId: 'TEST-ID',
              }),
            },
            queryParamMap: of(convertToParamMap({ reportType: 'Performance' })),
          },
        },
        { provide: SectorLevelPerformanceDataViewPagesService, useValue: mockService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = jest.spyOn(router, 'navigate');

    component = TestBed.createComponent(PerformanceDataReportTableComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call service on init', () => {
    expect(mockService.getSectorAccountPerformanceDataReportList).toHaveBeenCalledWith(
      1,
      expect.objectContaining({ pageNumber: 0, pageSize: 50 }),
    );
  });

  it('should navigate on page change', () => {
    component.onPageChange(2);

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({ page: 2 }),
        queryParamsHandling: 'merge',
      }),
    );
  });
});
