import { GovukTableColumn } from '@netz/govuk-components';

import { MiReportResult } from 'cca-api';

export const pageSize = 20;

export const miReportTypeDescriptionMap: Partial<Record<MiReportResult['reportType'], string>> = {
  CUSTOM: 'Custom SQL report',
};

export const miReportTypeLinkMap: Partial<Record<MiReportResult['reportType'], string[]>> = {
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
