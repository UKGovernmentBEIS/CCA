import { ExtendedMiReportResult } from '../core/mi-interfaces';

export const mockCustomMiReportResult: ExtendedMiReportResult = {
  reportType: 'CUSTOM',
  columnNames: ['id', 'name', 'competent_authority'],
  results: [
    { id: 1, name: 'Name 1', competent_authority: 'ENGLAND' },
    { id: 2, name: 'Name 2', competent_authority: 'WALES' },
  ],
};
