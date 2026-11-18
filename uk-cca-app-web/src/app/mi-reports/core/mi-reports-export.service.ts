import { Injectable } from '@angular/core';

import { utils, writeFileXLSX } from 'xlsx';

import { ExtendedMiReportResult } from './mi-interfaces';

@Injectable({ providedIn: 'root' })
export class MiReportsExportService {
  exportToExcel(miReportResult: ExtendedMiReportResult, filename: string) {
    const ws = utils.json_to_sheet(miReportResult.results);
    const wb = utils.book_new();

    utils.book_append_sheet(wb, ws, 'Data');
    writeFileXLSX(wb, `${filename}.xlsx`);
  }
}
