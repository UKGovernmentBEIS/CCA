import { inject, Injectable } from '@angular/core';

import { utils, writeFileXLSX } from 'xlsx';

import {
  SectorAccountPerformanceDataReportItemDTO,
  SectorLevelPerformanceAccountTemplateDataViewPagesService,
} from 'cca-api';
import { SectorLevelPerformanceDataViewPagesService } from 'cca-api';

import { PatCriteria } from '../pat/pat-report-form.provider';
import { PerformanceDataCriteria } from '../performance-data/performance-data-report-form.provider';

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
        const dataToExport: SectorAccountPerformanceDataReportItemDTO[] = resp.performanceDataReportItems.map(
          (item) => {
            delete item.accountId;
            return item;
          },
        );

        const ws = utils.json_to_sheet(dataToExport);
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
        const obj = resp.items.map((item) => {
          delete item.accountId;
          return item;
        });

        const ws = utils.json_to_sheet(obj);
        const wb = utils.book_new();

        utils.book_append_sheet(wb, ws, 'Data');
        writeFileXLSX(wb, 'pat_reporting.xlsx');
      });
  }
}
