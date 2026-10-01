import { inject, Injectable } from '@angular/core';

import { SpreadsheetExportService } from '@shared/services';

import { ExtendedMiReportResult } from './mi-interfaces';

@Injectable({ providedIn: 'root' })
export class MiReportsExportService {
  private readonly spreadsheetExportService = inject(SpreadsheetExportService);

  exportToExcel(miReportResult: ExtendedMiReportResult, filename: string) {
    this.spreadsheetExportService.exportToExcel(miReportResult.results, `${filename}.xlsx`);
  }
}
