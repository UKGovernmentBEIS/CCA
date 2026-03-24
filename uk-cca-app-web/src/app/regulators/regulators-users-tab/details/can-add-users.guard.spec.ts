import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UrlTree } from '@angular/router';

import { firstValueFrom, Observable, of } from 'rxjs';

import { AuthoritiesService, RegulatorAuthoritiesService } from 'cca-api';

import {
  executeRegulatorPermissions,
  permissionGroupLevels,
  readonlyRegulatorPermissions,
  regulatorRoles,
} from '../../testing/mock-http-responses';
import { CanAddUsers } from './can-add-users.guard';
import { DetailsStore } from './details.store';

describe('CanAddUsersGuard', () => {
  let authoritiesService: Partial<jest.Mocked<AuthoritiesService>>;
  let regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>>;

  beforeEach(() => {
    authoritiesService = {
      getRegulatorRoles: jest.fn().mockReturnValue(of(regulatorRoles)),
    };

    regulatorAuthoritiesService = {
      getRegulatorPermissionGroupLevels: jest.fn().mockReturnValue(of(permissionGroupLevels)),
      getCurrentRegulatorUserPermissionsByCa: jest.fn().mockReturnValue(of(null)),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        DetailsStore,
        {
          provide: AuthoritiesService,
          useValue: authoritiesService,
        },
        {
          provide: RegulatorAuthoritiesService,
          useValue: regulatorAuthoritiesService,
        },
      ],
    });
  });

  function getGuard() {
    return TestBed.runInInjectionContext(() => CanAddUsers(null, null));
  }

  it('should not let a user with no execute perms navigate to the route', async () => {
    regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa = jest
      .fn()
      .mockReturnValue(of(readonlyRegulatorPermissions));

    const res = await firstValueFrom((await getGuard()) as Observable<unknown>);
    expect(res instanceof UrlTree).toBeTruthy();
  });

  it('should patch state appropriately if given correct permissions', async () => {
    regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa = jest
      .fn()
      .mockReturnValue(of(executeRegulatorPermissions));

    const res = await firstValueFrom((await getGuard()) as Observable<unknown>);
    expect(res).toStrictEqual(true);
  });
});
