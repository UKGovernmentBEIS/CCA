import { AgreementTypePipe } from './agreement-type.pipe';

describe('AgreementTypePipe', () => {
  it('create an instance', () => {
    const pipe = new AgreementTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform agreement type', () => {
    const pipe = new AgreementTypePipe();
    let transformation: string;

    transformation = pipe.transform('ENVIRONMENTAL_PERMITTING_REGULATIONS');
    expect(transformation).toEqual('Environmental Permitting Regulations (EPR)');

    transformation = pipe.transform('ENERGY_INTENSIVE');
    expect(transformation).toEqual('Energy Intensive');
  });
});
