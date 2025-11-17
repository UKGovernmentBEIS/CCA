import { NoticeRecipientsTypePipe } from './notice-recipients-type.pipe';

describe('NoticeRecipientsTypePipe', () => {
  const pipe = new NoticeRecipientsTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map notice recipients types to text', () => {
    expect(pipe.transform('RESPONSIBLE_PERSON')).toEqual('Responsible person');
    expect(pipe.transform('ADMINISTRATIVE_CONTACT')).toEqual('Administrative contact');
    expect(pipe.transform('SECTOR_CONTACT')).toEqual('Sector contact');
    expect(pipe.transform('SECTOR_USER')).toEqual('Sector user');
    expect(pipe.transform('SECTOR_CONSULTANT')).toEqual('Sector consultant');
    expect(pipe.transform('OPERATOR')).toEqual('Operator');
    expect(pipe.transform(null)).toEqual('');
  });
});
