import { ContactTypePipe } from './contact-type.pipe';

describe('ContactTypePipe', () => {
  it('create an instance', () => {
    const pipe = new ContactTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform contact type', () => {
    const pipe = new ContactTypePipe();
    let transformation: string;

    transformation = pipe.transform('CONSULTANT');
    expect(transformation).toEqual(`Consultant`);

    transformation = pipe.transform('SECTOR_ASSOCIATION');
    expect(transformation).toEqual(`Sector association`);
  });
});
