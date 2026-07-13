import { FacilityPerformanceReportStatusPipe } from './facility-performance-report-status.pipe';

describe('FacilityPerformanceReportStatusPipe', () => {
  const pipe = new FacilityPerformanceReportStatusPipe();

  it('should format known report statuses', () => {
    expect(pipe.transform('TARGET_MET')).toBe('Target met');
  });

  it('should return unknown report statuses without throwing', () => {
    expect(pipe.transform('SERVER_STATUS')).toBe('SERVER_STATUS');
  });
});
