import { inject, Injectable } from '@angular/core';

import { boolToString } from '@requests/common';
import { utils, writeFileXLSX } from 'xlsx';

import {
  SectorAccountPerformanceDataReportItemDTO,
  SectorFacilityPerformanceDataReportItemDTO,
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorLevelPerformanceDataViewPagesService,
} from 'cca-api';

import { FacilityPerformanceDataCriteria } from '../facility-performance-data/facility-performance-data-report-form.provider';
import { FacilityPerformanceReportStatusEnum } from '../facility-performance-data/facility-performance-report-status.pipe';
import { PatCriteria } from '../pat/pat-report-form.provider';
import { PerformanceDataCriteria } from '../performance-data/performance-data-report-form.provider';

type AccountPerformanceDataExportItem = Omit<SectorAccountPerformanceDataReportItemDTO, 'accountId'>;
type ExportCellValue = string | number | null | undefined;
type ExportRow = Record<string, ExportCellValue>;

@Injectable({
  providedIn: 'root',
})
export class ReportingExportService {
  private readonly service = inject(SectorLevelPerformanceDataViewPagesService);
  private readonly sectorLevelPerformanceAccountTemplateDataViewPagesService = inject(
    SectorLevelPerformanceAccountTemplateDataViewPagesService,
  );

  exportPerformanceData(sectorId: number, filters: PerformanceDataCriteria, pageSize: number): void {
    this.service
      .getSectorAccountPerformanceDataReportList(sectorId, {
        performanceOutcome: filters.performanceOutcome,
        submissionType: filters.submissionType,
        targetUnitAccountBusinessId: filters.targetUnitAccountBusinessId,
        targetPeriodType: filters.targetPeriodType || 'TP6',
        pageNumber: 0,
        pageSize: pageSize,
      })
      .subscribe((resp) => {
        const dataToExport: AccountPerformanceDataExportItem[] = resp.performanceDataReportItems.map(
          ({ accountId: _accountId, ...item }) => item,
        );

        const ws = utils.json_to_sheet(dataToExport);
        const wb = utils.book_new();
        utils.book_append_sheet(wb, ws, 'Data');
        writeFileXLSX(wb, 'tp_reporting.xlsx');
      });
  }

  exportFacilityData(sectorId: number, filters: FacilityPerformanceDataCriteria, pageSize: number): void {
    this.service
      .getSectorFacilityPerformanceDataReportList(sectorId, {
        facilityOrTargetUnitAccountBusinessId: filters.facilityOrTargetUnitAccountBusinessId,
        targetPeriodType: filters.targetPeriodType,
        targetPeriodReportType: filters.targetPeriodReportType,
        reportStatus: filters.reportStatus,
        subType: filters.subType,
        pageNumber: 0,
        pageSize,
      })
      .subscribe((resp) => {
        const ws = utils.json_to_sheet(
          toFacilityPerformanceDataExportRows(
            resp.performanceDataReportItems ?? [],
            filters.targetPeriodReportType === 'FINAL',
          ),
        );
        const wb = utils.book_new();
        utils.book_append_sheet(wb, ws, 'Data');
        writeFileXLSX(wb, 'tp_reporting.xlsx');
      });
  }

  exportPatData(sectorId: number, criteria: PatCriteria): void {
    this.sectorLevelPerformanceAccountTemplateDataViewPagesService
      .getSectorPerformanceAccountTemplateDataReportList(sectorId, {
        targetUnitAccountBusinessId: criteria.targetUnitAccountBusinessId,
        targetPeriodType: criteria.targetPeriodType || 'TP6',
        submissionType: criteria.submissionType,
        status: criteria.status,
        pageNumber: criteria.pageNumber,
        pageSize: criteria.pageSize,
      })
      .subscribe((resp) => {
        const dataToExport = resp.items.map(({ accountId: _accountId, ...item }) => item);

        const ws = utils.json_to_sheet(dataToExport);
        const wb = utils.book_new();

        utils.book_append_sheet(wb, ws, 'Data');
        writeFileXLSX(wb, 'pat_reporting.xlsx');
      });
  }
}

export function toFacilityPerformanceDataExportRows(
  items: SectorFacilityPerformanceDataReportItemDTO[],
  includeFinalReportColumns: boolean,
): ExportRow[] {
  return items.map((item) => ({
    'Facility ID': item.facilityBusinessId,
    'Facility Name': item.siteName,
    'Date submitted': item.submissionDate,
    'Report version': item.reportVersion,
    Status: formatFacilityReportStatus(item.reportStatus),
    ...(includeFinalReportColumns
      ? {
          Subtype: formatEnumLabel(item.submissionType),
          Locked: formatLocked(item.locked),
        }
      : {}),
    'New variation': item.variationIndicator ? 'Yes' : '',
    '70% confirmation (Yes or No)': boolToString(item.atLeastSeventyPercentEnergyUsed),
    'Performance against target (%)': formatExportNumber(item.actualImprovement),
    'Actual Primary energy or carbon used': formatExportNumber(item.actualEnergyCarbon),
    'Target energy': formatExportNumber(item.targetEnergyCarbon),
    'Energy difference': formatExportNumber(item.energyCarbonDifference),
    'Actual tCO2e': formatExportNumber(item.actualCo2Emissions),
    'Target tCO2e': formatExportNumber(item.targetCo2Emissions),
    'tCO2e difference': formatExportNumber(item.co2EmissionsDifference),
    'Total buy-out (tCO2e)': formatExportNumber(item.buyOutRequired),
    'Surplus gained (tCO2e)': formatExportNumber(item.surplusGained),
  }));
}

function formatFacilityReportStatus(value: SectorFacilityPerformanceDataReportItemDTO['reportStatus']): string | null {
  return value ? (FacilityPerformanceReportStatusEnum[value] ?? value) : null;
}

function formatLocked(value: boolean | null | undefined): 'Y' | 'N' {
  return value ? 'Y' : 'N';
}

function formatEnumLabel(value: string | null | undefined): string | null {
  return value
    ? value
        .toLowerCase()
        .split('_')
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ')
    : null;
}

function formatExportNumber(value: string | null | undefined): string | null | undefined {
  return value?.match(/^[-+]?0(?:\.0+)?e[-+]?\d+$/i) ? '0' : value;
}
