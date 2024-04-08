import { utils, writeFileXLSX } from 'xlsx';

import { GovukTableColumn } from 'govuk-components';

import { MiReportResult } from 'cca-api';

import { ExtendedMiReportResult } from './mi-interfaces';

export const pageSize = 20;

export const miReportTypeDescriptionMap: Record<'CUSTOM' | 'LIST_OF_ACCOUNTS_USERS_CONTACTS', string> = {
  LIST_OF_ACCOUNTS_USERS_CONTACTS: 'List of Accounts, Users and Contacts',
  CUSTOM: 'Custom sql report',
};

export const miReportTypeLinkMap: Partial<Record<MiReportResult['reportType'], string[]>> = {
  LIST_OF_ACCOUNTS_USERS_CONTACTS: ['./', 'accounts-users-contacts'],
  CUSTOM: ['./', 'custom'],
};

export const createTablePage = (currentPage: number, pageSize: number, data: any[]): any[] => {
  const firstIndex = (currentPage - 1) * pageSize;
  const lastIndex = Math.min(firstIndex + pageSize, data?.length);

  return data?.length > firstIndex ? data.slice(firstIndex, lastIndex) : [];
};

export const createTableColumns = (columns: string[]): GovukTableColumn<any>[] => {
  return columns.map((column) => ({ field: column, header: column }));
};

export const manipulateResultsAndExportToExcel = (
  miReportResult: ExtendedMiReportResult,
  filename: string,
  manipulateResultsFn?: (
    parameter: {
      [x: string]: any;
    }[],
  ) => {
    [x: string]: any;
  }[],
) => {
  const removedColumnsResults = miReportResult.results.map((result) =>
    miReportResult.columnNames
      .map((columnName) => ({ [columnName]: result[columnName] }))
      .reduce((prev, cur) => ({ ...prev, ...cur }), {}),
  );

  const results = manipulateResultsFn ? manipulateResultsFn(removedColumnsResults) : removedColumnsResults;
  const ws = utils.json_to_sheet(results);
  const wb = utils.book_new();

  utils.book_append_sheet(wb, ws, 'Data');
  writeFileXLSX(wb, `${filename}.xlsx`);
};
