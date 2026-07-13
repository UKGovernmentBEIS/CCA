import { MeasurementTypeToOptionTextPipe, transformMeasurementType } from './measurement-type-to-option-text.pipe';

describe('MeasurementTypeToOptionTextPipe', () => {
  const pipe = new MeasurementTypeToOptionTextPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform ENERGY_KWH to Energy (kWh)', () => {
    expect(pipe.transform('ENERGY_KWH')).toBe('Energy (kWh)');
  });

  it('should transform ENERGY_MWH to Energy (MWh)', () => {
    expect(pipe.transform('ENERGY_MWH')).toBe('Energy (MWh)');
  });

  it('should transform ENERGY_GJ to Energy (GJ)', () => {
    expect(pipe.transform('ENERGY_GJ')).toBe('Energy (GJ)');
  });

  it('should transform CARBON_KG to Carbon (kg)', () => {
    expect(pipe.transform('CARBON_KG')).toBe('Carbon (kg)');
  });

  it('should transform CARBON_TONNE to Carbon (tonne)', () => {
    expect(pipe.transform('CARBON_TONNE')).toBe('Carbon (tonne)');
  });

  it('should throw an error for invalid input', () => {
    expect(() => pipe.transform('INVALID')).toThrow('Invalid measurement type');
  });
});

describe('transformMeasurementType', () => {
  it('should map ENERGY_KWH to Energy (kWh)', () => {
    expect(transformMeasurementType('ENERGY_KWH')).toBe('Energy (kWh)');
  });

  it('should map ENERGY_MWH to Energy (MWh)', () => {
    expect(transformMeasurementType('ENERGY_MWH')).toBe('Energy (MWh)');
  });

  it('should map ENERGY_GJ to Energy (GJ)', () => {
    expect(transformMeasurementType('ENERGY_GJ')).toBe('Energy (GJ)');
  });

  it('should map CARBON_KG to Carbon (kg)', () => {
    expect(transformMeasurementType('CARBON_KG')).toBe('Carbon (kg)');
  });

  it('should map CARBON_TONNE to Carbon (tonne)', () => {
    expect(transformMeasurementType('CARBON_TONNE')).toBe('Carbon (tonne)');
  });

  it('should throw an error for invalid input', () => {
    expect(() => transformMeasurementType('INVALID')).toThrow('Invalid measurement type');
  });
});
