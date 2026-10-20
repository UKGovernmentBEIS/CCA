import { SectorFacilityPerformanceDataReportItemDTO } from 'cca-api';

import { toFacilityPerformanceDataExportRows } from './reporting-export.service';

describe('ReportingExportService', () => {
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
});
