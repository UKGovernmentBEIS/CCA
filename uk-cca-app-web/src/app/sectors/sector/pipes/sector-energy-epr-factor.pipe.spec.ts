import { SectorEnergyEprFactorPipe } from './sector-energy-epr-factor.pipe';

describe('SectorEnergyEprFactorPipe', () => {
  const pipe = new SectorEnergyEprFactorPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct factor type', () => {
    expect(pipe.transform('ENVIRONMENTAL_PERMITTING_REGULATIONS')).toEqual('EPR');
    expect(pipe.transform('ENERGY_INTENSIVE')).toEqual('Energy intensive');
  });
});
