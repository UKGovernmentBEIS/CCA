import { GovukTableColumn } from '@netz/govuk-components';

import { MiReportResult } from 'cca-api';

export const pageSize = 20;

export const miReportTypeLinkMap: Partial<Record<MiReportResult['reportType'], string[]>> = {
  CUSTOM: ['./', 'custom'],
};

export const createTablePage = <T>(currentPage: number, pageSize: number, data: T[]): T[] => {
  const firstIndex = (currentPage - 1) * pageSize;
  const lastIndex = Math.min(firstIndex + pageSize, data?.length);

  return data?.length > firstIndex ? data.slice(firstIndex, lastIndex) : [];
};

export const createTableColumns = (columns: string[]): GovukTableColumn<Record<string, unknown>>[] => {
  return columns.map((column) => ({ field: column, header: column }));
};
