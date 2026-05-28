import { toAppealDetailsSummaryData } from './to-appeal-details-summary-data';

describe('toAppealDetailsSummaryData', () => {
  it('should return appeal details summary rows', () => {
    const summaryData = toAppealDetailsSummaryData(
      {
        registrationDate: '2026-01-01',
        files: ['uuid-1'],
        comments: 'Appeal comments',
      },
      { 'uuid-1': 'appeal.pdf' },
      true,
      '/tasks/123/file-download',
    );

    expect(summaryData).toHaveLength(1);
    expect(summaryData[0].data).toEqual([
      {
        key: 'When was the appeal registered?',
        value: ['1 Jan 2026'],
        change: true,
        changeLink: '../provide-details',
      },
      {
        key: 'Appeal file',
        value: [{ fileName: 'appeal.pdf', downloadUrl: '/tasks/123/file-download/uuid-1' }],
        isFileList: true,
        change: true,
        changeLink: '../provide-details',
      },
      {
        key: 'Comments',
        value: ['Appeal comments'],
        preline: true,
        change: true,
        changeLink: '../provide-details',
      },
    ]);
  });

  it('should hide change links when not editable', () => {
    const summaryData = toAppealDetailsSummaryData(
      {
        registrationDate: '2026-01-01',
        files: [],
        comments: null,
      },
      {},
      false,
      '/tasks/123/file-download',
    );

    expect(summaryData[0].data.every((row) => row.change === false)).toBe(true);
  });
});
