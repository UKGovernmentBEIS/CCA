import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { SpreadsheetExportService } from '@shared/services';

import {
  SectorFacilityPerformanceDataReportItemDTO,
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorLevelPerformanceDataViewPagesService,
} from 'cca-api';

import { mockPatAccountsReport, mockPatFacilitiesReport } from '../pat/testing/mock-data';
import {
  ReportingExportService,
  toFacilityPerformanceDataExportRows,
  toPatExportRows,
} from './reporting-export.service';

describe('ReportingExportService', () => {
  it.each([
    {
      year: 2024,
      method: 'getSectorAccountPerformanceAccountTemplateDataReportList' as const,
      response: mockPatAccountsReport,
    },
    {
      year: 2026,
      method: 'getSectorFacilityPerformanceAccountTemplateDataReportList' as const,
      response: mockPatFacilitiesReport,
    },
  ])('downloads all filtered $year PAT results as CSV', ({ year, method, response }) => {
    const spreadsheetExportService = { exportToCsv: vi.fn() };
    const api = {
      getSectorAccountPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatAccountsReport)),
      getSectorFacilityPerformanceAccountTemplateDataReportList: vi.fn().mockReturnValue(of(mockPatFacilitiesReport)),
    };
    TestBed.configureTestingModule({
      providers: [
        { provide: SpreadsheetExportService, useValue: spreadsheetExportService },
        { provide: SectorLevelPerformanceAccountTemplateDataViewPagesService, useValue: api },
        { provide: SectorLevelPerformanceDataViewPagesService, useValue: {} },
      ],
    });

    TestBed.inject(ReportingExportService).exportPatData(
      1,
      {
        term: 'ADS',
        targetPeriodYear: year,
        status: 'OUTSTANDING',
        pageNumber: 2,
        pageSize: 50,
      },
      125,
    );

    expect(api[method]).toHaveBeenCalledWith(1, {
      term: 'ADS',
      targetPeriodYear: year,
      status: 'OUTSTANDING',
      pageNumber: 0,
      pageSize: 125,
    });
    const otherMethod =
      year === 2024
        ? api.getSectorFacilityPerformanceAccountTemplateDataReportList
        : api.getSectorAccountPerformanceAccountTemplateDataReportList;
    expect(otherMethod).not.toHaveBeenCalled();
    expect(spreadsheetExportService.exportToCsv).toHaveBeenCalledExactlyOnceWith(
      toPatExportRows(response.items, year !== 2024),
      'pat_reporting.csv',
    );
  });

  const facilityReportItem = {
    accountId: 1,
    facilityId: 10,
    facilityBusinessId: 'ADS-F0001',
    siteName: 'Test Facility',
    submissionDate: '2027-04-25T00:00:00Z',
    reportVersion: 3,
    reportStatus: 'TARGET_NOT_MET',
    submissionType: 'SECONDARY',
    locked: false,
    variationIndicator: true,
    atLeastSeventyPercentEnergyUsed: true,
    actualImprovement: '3',
    actualEnergyCarbon: '1200',
    targetEnergyCarbon: '1000',
    energyCarbonDifference: '200',
    targetCo2Emissions: '900',
    actualCo2Emissions: '950',
    co2EmissionsDifference: '50',
    buyOutRequired: '150',
    surplusGained: '0',
  } satisfies SectorFacilityPerformanceDataReportItemDTO;

  it('formats final facility performance export rows with named columns', () => {
    const rows = toFacilityPerformanceDataExportRows([facilityReportItem], true);

    expect(Object.keys(rows[0])).toEqual([
      'Facility ID',
      'Facility Name',
      'Date submitted',
      'Report version',
      'Status',
      'Subtype',
      'Locked',
      'New variation',
      '70% confirmation (Yes or No)',
      'Performance against target (%)',
      'Actual Primary energy or carbon used',
      'Target energy',
      'Energy difference',
      'Actual tCO2e',
      'Target tCO2e',
      'tCO2e difference',
      'Total buy-out (tCO2e)',
      'Surplus gained (tCO2e)',
    ]);
    expect(rows[0]).toEqual({
      'Facility ID': 'ADS-F0001',
      'Facility Name': 'Test Facility',
      'Date submitted': '2027-04-25T00:00:00Z',
      'Report version': 3,
      Status: 'Target not met',
      Subtype: 'Secondary',
      Locked: 'N',
      'New variation': 'Yes',
      '70% confirmation (Yes or No)': 'Yes',
      'Performance against target (%)': '3',
      'Actual Primary energy or carbon used': '1200',
      'Target energy': '1000',
      'Energy difference': '200',
      'Actual tCO2e': '950',
      'Target tCO2e': '900',
      'tCO2e difference': '50',
      'Total buy-out (tCO2e)': '150',
      'Surplus gained (tCO2e)': '0',
    });
  });

  it('omits final-only columns from interim facility performance export rows', () => {
    const rows = toFacilityPerformanceDataExportRows(
      [
        {
          ...facilityReportItem,
          reportStatus: 'SUBMITTED',
          submissionType: undefined,
          locked: undefined,
        },
      ],
      false,
    );

    expect(Object.keys(rows[0])).not.toContain('Subtype');
    expect(Object.keys(rows[0])).not.toContain('Locked');
    expect(rows[0]).toEqual(
      expect.objectContaining({
        Status: 'Submitted',
        '70% confirmation (Yes or No)': 'Yes',
      }),
    );
  });

  it('formats zero values returned in scientific notation as zero', () => {
    const [row] = toFacilityPerformanceDataExportRows(
      [
        {
          ...facilityReportItem,
          actualImprovement: '0E-7',
          actualEnergyCarbon: '0E-7',
          targetEnergyCarbon: '0E-7',
          energyCarbonDifference: '0E-7',
          actualCo2Emissions: '0E-7',
          targetCo2Emissions: '0E-7',
          co2EmissionsDifference: '0E-7',
          buyOutRequired: '0E-7',
          surplusGained: '0E-7',
        },
      ],
      true,
    );

    expect(row).toEqual(
      expect.objectContaining({
        'Performance against target (%)': '0',
        'Actual Primary energy or carbon used': '0',
        'Target energy': '0',
        'Energy difference': '0',
        'Actual tCO2e': '0',
        'Target tCO2e': '0',
        'tCO2e difference': '0',
        'Total buy-out (tCO2e)': '0',
        'Surplus gained (tCO2e)': '0',
      }),
    );
  });

  it('labels PAT export rows by facility for CCA3 years', () => {
    const rows = toPatExportRows(
      [
        {
          id: 40,
          businessId: 'ADS-F00040',
          name: 'Facility 40',
          submissionDate: '2027-04-25T00:00:00',
          status: 'SUBMITTED',
        },
      ],
      true,
    );

    expect(rows[0]).toEqual({
      'Facility ID': 'ADS-F00040',
      'Facility site name': 'Facility 40',
      'Date submitted': '2027-04-25T00:00:00',
      Status: 'Submitted',
    });
  });

  it('labels PAT export rows by target unit for TP6', () => {
    const rows = toPatExportRows(
      [{ id: 1, businessId: 'ADS-T00040', name: 'Operator name', status: 'OUTSTANDING' }],
      false,
    );

    expect(rows[0]).toEqual({
      'Target unit ID': 'ADS-T00040',
      Operator: 'Operator name',
      'Date submitted': undefined,
      Status: 'Outstanding',
    });
  });
});
