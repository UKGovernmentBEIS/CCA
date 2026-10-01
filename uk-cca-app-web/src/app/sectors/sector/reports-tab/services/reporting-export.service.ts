import { TitleCasePipe } from '@angular/common';
import { inject, Injectable } from '@angular/core';

import { boolToString } from '@requests/common';
import { SpreadsheetExportService } from '@shared/services';
import { formatScientificZero } from '@shared/utils';

import {
  SectorAccountPerformanceDataReportItemDTO,
  SectorFacilityPerformanceDataReportItemDTO,
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
  SectorLevelPerformanceDataViewPagesService,
} from 'cca-api';

import { FacilityPerformanceDataCriteria } from '../facility-performance-data/facility-performance-data-report-form.provider';
import { FacilityPerformanceReportStatusEnum } from '../facility-performance-data/facility-performance-report-status.pipe';
import { isFacilityPatYear, PatCriteria, PatReportItem } from '../pat/pat-report-form.provider';
import { PerformanceDataCriteria } from '../performance-data/performance-data-report-form.provider';

type AccountPerformanceDataExportItem = Omit<SectorAccountPerformanceDataReportItemDTO, 'accountId'>;
type ExportCellValue = string | number | null | undefined;
type ExportRow = Record<string, ExportCellValue>;

@Injectable({
  providedIn: 'root',
})
export class ReportingExportService {
  private readonly spreadsheetExportService = inject(SpreadsheetExportService);
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

        this.spreadsheetExportService.exportToExcel(dataToExport, 'tp_reporting.xlsx');
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
        this.spreadsheetExportService.exportToExcel(
          toFacilityPerformanceDataExportRows(
            resp.performanceDataReportItems ?? [],
            filters.targetPeriodReportType === 'FINAL',
          ),
          'tp_reporting.xlsx',
        );
      });
  }

  exportPatData(sectorId: number, criteria: PatCriteria, pageSize: number): void {
    const isFacilityReport = isFacilityPatYear(criteria.targetPeriodYear);
    const request = {
      term: criteria.term,
      targetPeriodYear: criteria.targetPeriodYear,
      status: criteria.status,
      pageNumber: 0,
      pageSize,
    };

    const report$ = isFacilityReport
      ? this.sectorLevelPerformanceAccountTemplateDataViewPagesService.getSectorFacilityPerformanceAccountTemplateDataReportList(
          sectorId,
          request,
        )
      : this.sectorLevelPerformanceAccountTemplateDataViewPagesService.getSectorAccountPerformanceAccountTemplateDataReportList(
          sectorId,
          request,
        );

    report$.subscribe((resp) => {
      this.spreadsheetExportService.exportToCsv(
        toPatExportRows(resp.items ?? [], isFacilityReport),
        'pat_reporting.csv',
      );
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
    'Performance against target (%)': formatScientificZero(item.actualImprovement),
    'Actual Primary energy or carbon used': formatScientificZero(item.actualEnergyCarbon),
    'Target energy': formatScientificZero(item.targetEnergyCarbon),
    'Energy difference': formatScientificZero(item.energyCarbonDifference),
    'Actual tCO2e': formatScientificZero(item.actualCo2Emissions),
    'Target tCO2e': formatScientificZero(item.targetCo2Emissions),
    'tCO2e difference': formatScientificZero(item.co2EmissionsDifference),
    'Total buy-out (tCO2e)': formatScientificZero(item.buyOutRequired),
    'Surplus gained (tCO2e)': formatScientificZero(item.surplusGained),
  }));
}

export function toPatExportRows(items: PatReportItem[], isFacilityReport: boolean): ExportRow[] {
  const titleCasePipe = new TitleCasePipe();

  return items.map((item) => ({
    ...(isFacilityReport
      ? { 'Facility ID': item.businessId, 'Facility site name': item.name }
      : { 'Target unit ID': item.businessId, Operator: item.name }),
    'Date submitted': item.submissionDate,
    Status: titleCasePipe.transform(item.status),
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
