import { ExtendedMiReportResult } from '../core/mi-interfaces';

export const mockCustomMiReportResult: ExtendedMiReportResult = {
  reportType: 'CUSTOM',
  columnNames: ['id', 'name', 'competent_authority'],
  results: [
    { id: { value: 1 }, name: { value: 'Name 1' }, competent_authority: { value: 'ENGLAND' } },
    { id: { value: 2 }, name: { value: 'Name 2' }, competent_authority: { value: 'WALES' } },
  ],
};
