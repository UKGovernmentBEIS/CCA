import { GovukDatePipe } from '@netz/common/pipes';

import { DurationPipe } from './duration-pipe';

describe('DurationPipe', () => {
  let pipe: DurationPipe;
  let govukDatePipeSpy: jest.SpyInstance;

  beforeEach(() => {
    // Spy on GovukDatePipe methods
    govukDatePipeSpy = jest.spyOn(GovukDatePipe.prototype, 'transform');

    pipe = new DurationPipe();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should format a valid start and end date', () => {
    govukDatePipeSpy.mockImplementationOnce(() => '1 Jan 2024').mockImplementationOnce(() => '31 Jan 2024');

    const result = pipe.transform('2024-01-01', '2024-01-31');
    expect(result).toBe('1 Jan 2024 to 31 Jan 2024');
    expect(govukDatePipeSpy).toHaveBeenCalledTimes(2);
  });

  it('should return an empty string if startDate is missing', () => {
    const result = pipe.transform('', '2024-01-31');
    expect(result).toBe('');
    expect(govukDatePipeSpy).not.toHaveBeenCalled();
  });

  it('should return an empty string if endDate is missing', () => {
    const result = pipe.transform('2024-01-01', '');
    expect(result).toBe('');
    expect(govukDatePipeSpy).not.toHaveBeenCalled();
  });

  it('should return an empty string if both dates are missing', () => {
    const result = pipe.transform('', '');
    expect(result).toBe('');
    expect(govukDatePipeSpy).not.toHaveBeenCalled();
  });
});
