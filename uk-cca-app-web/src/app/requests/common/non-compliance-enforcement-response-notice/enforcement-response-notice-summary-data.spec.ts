import { toEnforcementResponseNoticeSummaryData } from './enforcement-response-notice-summary-data';

describe('toEnforcementResponseNoticeSummaryData', () => {
  const enforcementResponseNotice = {
    type: 'PENALTY' as const,
    file: 'uuid-1',
    comments: 'Existing comments',
  };
  const attachments = { 'uuid-1': 'existing-notice.pdf' };

  it('should include the enforcement response notice type when this is not a penalty reissue', () => {
    const summaryData = toEnforcementResponseNoticeSummaryData(
      enforcementResponseNotice,
      attachments,
      false,
      './file-download',
      false,
    );

    expect(summaryData[0].data.map((row) => row.key)).toEqual([
      'Type of enforcement response notice',
      'Upload file',
      'Your comments',
    ]);
  });

  it('should hide the enforcement response notice type for a penalty reissue', () => {
    const summaryData = toEnforcementResponseNoticeSummaryData(
      enforcementResponseNotice,
      attachments,
      false,
      './file-download',
      true,
    );

    expect(summaryData[0].data.map((row) => row.key)).toEqual(['Upload file', 'Your comments']);
  });
});
