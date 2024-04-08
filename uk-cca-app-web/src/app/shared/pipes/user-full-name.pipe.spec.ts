import { fakeAsync, TestBed } from '@angular/core/testing';

import { ApplicationUserDTO } from 'cca-api';

import { UserFullNamePipe } from './user-full-name.pipe';

describe('UserFullNamePipe', () => {
  let pipe: UserFullNamePipe;
  const testUser: Partial<ApplicationUserDTO> = {
    firstName: 'CD',
    lastName: 'PR',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserFullNamePipe],
    });
  });

  beforeEach(() => (pipe = new UserFullNamePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the country code and calling code', fakeAsync(() => {
    expect(pipe.transform(testUser as ApplicationUserDTO)).toEqual('CD PR');
  }));
});
