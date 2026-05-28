import { FuelTypePipe } from './fuel-type.pipe';

describe('FuelTypePipe', () => {
  const pipe = new FuelTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map fuel types to labels', () => {
    expect(pipe.transform('GRID_ELECTRICITY')).toEqual(
      'Grid electricity and electricity from combustion of a renewable fuel',
    );
    expect(pipe.transform('NON_GRID_ELECTRICITY')).toEqual(
      'Non-grid electricity from renewable sources (PV, hydro and wind)',
    );
    expect(pipe.transform('NATURAL_GAS')).toEqual('Natural gas');
    expect(pipe.transform('LPG')).toEqual('LPG');
    expect(pipe.transform('GAS_DIESEL_OIL')).toEqual('Gas oil/Diesel');
    expect(pipe.transform('FUEL_OIL')).toEqual('Fuel Oil');
    expect(pipe.transform('KEROSENE')).toEqual('Kerosene');
    expect(pipe.transform('COAL')).toEqual('Coal');
    expect(pipe.transform('COKE')).toEqual('Coke');
    expect(pipe.transform('PETROL')).toEqual('Petrol');
    expect(pipe.transform('NITROGEN_COOLING')).toEqual('Nitrogen cooling');
    expect(pipe.transform('CARBON_DIOXIDE_COOLING')).toEqual('Carbon dioxide cooling');
    expect(pipe.transform('ETHANE')).toEqual('Ethane');
    expect(pipe.transform('NAPHTHA')).toEqual('Naphtha');
    expect(pipe.transform('PETROLEUM_COKE')).toEqual('Petroleum Coke');
    expect(pipe.transform('REFINERY_GAS')).toEqual('Refinery Gas');
  });
});
