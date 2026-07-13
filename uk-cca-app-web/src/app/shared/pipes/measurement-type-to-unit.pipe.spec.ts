import { MeasurementTypeToUnitPipe, transformMeasurementTypeToUnit } from './measurement-type-to-unit.pipe';

describe('MeasurementTypeToUnitPipe', () => {
  const pipe = new MeasurementTypeToUnitPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform ENERGY_KWH to kWh', () => {
    expect(pipe.transform('ENERGY_KWH')).toBe('kWh');
  });

  it('should transform ENERGY_MWH to MWh', () => {
    expect(pipe.transform('ENERGY_MWH')).toBe('MWh');
  });

  it('should transform ENERGY_GJ to GJ', () => {
    expect(pipe.transform('ENERGY_GJ')).toBe('GJ');
  });

  it('should transform CARBON_KG to kg', () => {
    expect(pipe.transform('CARBON_KG')).toBe('kg');
  });

  it('should transform CARBON_TONNE to tonne', () => {
    expect(pipe.transform('CARBON_TONNE')).toBe('tonne');
  });

  it('should throw an error for invalid input', () => {
    expect(() => pipe.transform('INVALID')).toThrow('Invalid measurement type');
  });
});

describe('transformMeasurementTypeToUnit', () => {
  it('should map ENERGY_KWH to kWh', () => {
    expect(transformMeasurementTypeToUnit('ENERGY_KWH')).toBe('kWh');
  });

  it('should map ENERGY_MWH to MWh', () => {
    expect(transformMeasurementTypeToUnit('ENERGY_MWH')).toBe('MWh');
  });

  it('should map ENERGY_GJ to GJ', () => {
    expect(transformMeasurementTypeToUnit('ENERGY_GJ')).toBe('GJ');
  });

  it('should map CARBON_KG to kg', () => {
    expect(transformMeasurementTypeToUnit('CARBON_KG')).toBe('kg');
  });

  it('should map CARBON_TONNE to tonne', () => {
    expect(transformMeasurementTypeToUnit('CARBON_TONNE')).toBe('tonne');
  });

  it('should throw an error for invalid input', () => {
    expect(() => transformMeasurementTypeToUnit('INVALID')).toThrow('Invalid measurement type');
  });
});
