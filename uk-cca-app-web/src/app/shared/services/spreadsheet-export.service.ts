import { Injectable } from '@angular/core';

import { utils, writeFile, writeFileXLSX } from 'xlsx';

@Injectable({ providedIn: 'root' })
export class SpreadsheetExportService {
  exportToCsv<T>(rows: T[], filename: string, sheetName = 'Data'): void {
    writeFile(this.createWorkbook(rows, sheetName), filename, { bookType: 'csv' });
  }

  exportToExcel<T>(rows: T[], filename: string, sheetName = 'Data'): void {
    writeFileXLSX(this.createWorkbook(rows, sheetName), filename);
  }

  private createWorkbook<T>(rows: T[], sheetName: string) {
    const workbook = utils.book_new();
    utils.book_append_sheet(workbook, utils.json_to_sheet(rows), sheetName);
    return workbook;
  }
}
