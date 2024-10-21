import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UrlTree } from '@angular/router';

import { firstValueFrom, Observable, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteSnapshotStub } from '@netz/common/testing';

import { AuthoritiesService, RegulatorAuthoritiesService, RegulatorUsersService, UsersService } from 'cca-api';

import {
  executeRegulatorPermissions,
  permissionGroupLevels,
  readonlyCurrentUser,
  readonlyRegulatorPermissions,
  regulatorRoles,
} from '../../testing/mock-http-responses';
import { CanEditUserGuard } from './can-edit-user.guard';
import { DetailsStore } from './details.store';

describe('CanEditUsersGuard', () => {
  let detailsStore: DetailsStore;
  let authStore: AuthStore;
  let authoritiesService: Partial<jest.Mocked<AuthoritiesService>> = {};
  let regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {};
  let regulatorUserService: Partial<jest.Mocked<RegulatorUsersService>> = {};
  const usersService: Partial<jest.Mocked<UsersService>> = {};

  const regulatorUserId = '12309123';
  const routeStub1 = new ActivatedRouteSnapshotStub({ userId: regulatorUserId });
  const routeStub2 = new ActivatedRouteSnapshotStub({ userId: regulatorUserId + '2' });

  beforeEach(() => {
    authoritiesService = {
      getRegulatorRoles: jest.fn().mockReturnValue(of(regulatorRoles)),
    };

    regulatorAuthoritiesService = {
      getCurrentRegulatorUserPermissionsByCa: jest.fn().mockReturnValue(of(executeRegulatorPermissions)),
      getRegulatorPermissionGroupLevels: jest.fn().mockReturnValue(of(permissionGroupLevels)),
    };

    regulatorUserService = {
      getRegulatorUserByCaAndId: jest.fn().mockReturnValue(of(readonlyCurrentUser)),
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        DetailsStore,
        {
          provide: AuthoritiesService,
          useValue: authoritiesService,
        },
        {
          provide: RegulatorUsersService,
          useValue: regulatorUserService,
        },
        {
          provide: UsersService,
          useValue: usersService,
        },
        {
          provide: RegulatorAuthoritiesService,
          useValue: regulatorAuthoritiesService,
        },
      ],
    });

    detailsStore = TestBed.inject(DetailsStore);

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'REGULATOR',
      status: 'ENABLED',
      userId: regulatorUserId,
    });
  });

  function getGuard(route: ActivatedRouteSnapshotStub) {
    return TestBed.runInInjectionContext(() => CanEditUserGuard(route, null));
  }

  it("should NOT let a user with no execute perms that tries to navigate to other user' details", async () => {
    regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa = jest
      .fn()
      .mockReturnValue(of(readonlyRegulatorPermissions));
    const res = await firstValueFrom((await getGuard(routeStub2)) as Observable<unknown>);
    expect(res).toBeInstanceOf(UrlTree);
  });

  it('should let a user with no execute perms to navigate to their own user details', async () => {
    usersService.getCurrentUser = jest.fn().mockReturnValue(of(readonlyCurrentUser));
    regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa = jest
      .fn()
      .mockReturnValue(of(readonlyRegulatorPermissions));
    const res = await firstValueFrom((await getGuard(routeStub1)) as Observable<unknown>);
    expect(res).toStrictEqual(true);
  });

  it('should correctly populate state if a user has EXECUTE perms', async () => {
    regulatorAuthoritiesService.getRegulatorUserPermissionsByCaAndId = jest
      .fn()
      .mockReturnValue(of(executeRegulatorPermissions));
    const res = await firstValueFrom((await getGuard(routeStub2)) as Observable<unknown>);
    expect(res).toStrictEqual(true);
    expect(res).toMatchSnapshot();
    expect(detailsStore.state).toMatchSnapshot();
  });
});
