import { FacilityPatReportingTypesPipe } from './facility-pat-reporting-types.pipe';

describe('FacilityPatReportingTypesPipe', () => {
  const pipe = new FacilityPatReportingTypesPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('correctly transform values', () => {
    expect(pipe.transform('ENERGY_MANAGEMENT')).toBe('Energy management');
    expect(pipe.transform('NEW_TECHNOLOGY_UPTAKE')).toBe('New technology uptake');
    expect(pipe.transform('FIXED_AND_VARIABLE')).toBe('Fixed and variable');
    expect(pipe.transform('FIXED')).toBe('Fixed');
  });
});
