import { inject, Service } from '@angular/core';

import { SpreadsheetExportService } from '@shared/services';

import { ExtendedMiReportResult } from './mi-interfaces';

@Service()
export class MiReportsExportService {
  private readonly spreadsheetExportService = inject(SpreadsheetExportService);

  exportToExcel(miReportResult: ExtendedMiReportResult, filename: string) {
    this.spreadsheetExportService.exportToExcel(miReportResult.results, `${filename}.xlsx`);
  }
}
