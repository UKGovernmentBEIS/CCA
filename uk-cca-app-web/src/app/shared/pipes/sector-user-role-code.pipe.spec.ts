import { SectorUserRoleCodePipe } from './sector-user-role-code.pipe';

describe('SectorUserRoleCodePipe', () => {
  it('create an instance', () => {
    const pipe = new SectorUserRoleCodePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform role code', () => {
    const pipe = new SectorUserRoleCodePipe();
    let transformation: string;

    transformation = pipe.transform('sector_user_administrator');
    expect(transformation).toEqual(`Administrator user`);

    transformation = pipe.transform('sector_user_basic_user');
    expect(transformation).toEqual(`Basic user`);
  });
});
