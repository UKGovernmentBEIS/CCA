import { CaNamePipe } from './ca-name.pipe';

describe('CaNamePipe', () => {
  it('create an instance', () => {
    const pipe = new CaNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform ca name', () => {
    const pipe = new CaNamePipe();
    let transformation: string;

    transformation = pipe.transform('ENVIRONMENT_AGENCY');
    expect(transformation).toEqual('Environment Agency (England)');

    transformation = pipe.transform('SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY');
    expect(transformation).toEqual('Scottish Environment Protection Agency (Scotland)');

    transformation = pipe.transform('DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS');
    expect(transformation).toEqual('Department of Agriculture, Environment and Rural Affairs (Northern Ireland)');

    transformation = pipe.transform('NATURAL_RESOURCES_WALES');
    expect(transformation).toEqual('Natural Resources Wales (Wales)');

    transformation = pipe.transform('OTHER');
    expect(transformation).toEqual('Other');
  });
});
