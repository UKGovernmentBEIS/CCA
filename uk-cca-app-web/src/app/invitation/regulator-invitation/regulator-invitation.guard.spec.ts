import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { lastValueFrom, of, throwError } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass } from '@netz/common/testing';

import { InvitedUserInfoDTO, RegulatorUsersRegistrationService } from 'cca-api';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';
import { RegulatorInvitationGuard } from './regulator-invitation.guard';

describe('RegulatorInvitationGuard', () => {
  let router: Router;
  let store: InvitedRegulatorUserStore;
  let regulatorUsersRegistrationService: jest.Mocked<RegulatorUsersRegistrationService>;

  beforeEach(() => {
    regulatorUsersRegistrationService = mockClass(RegulatorUsersRegistrationService);

    TestBed.configureTestingModule({
      providers: [
        { provide: RegulatorUsersRegistrationService, useValue: regulatorUsersRegistrationService },
        InvitedRegulatorUserStore,
      ],
    });

    router = TestBed.inject(Router);
    store = TestBed.inject(InvitedRegulatorUserStore);
  });

  function getGuard(route: ActivatedRouteSnapshot) {
    return TestBed.runInInjectionContext(() => RegulatorInvitationGuard(route));
  }

  it('should navigate to root if there is no token query param', async () => {
    await expect(lastValueFrom(getGuard(new ActivatedRouteSnapshotStub()))).resolves.toEqual(
      router.parseUrl('landing'),
    );

    expect(regulatorUsersRegistrationService.acceptRegulatorInvitation).not.toHaveBeenCalled();
  });

  it('should navigate to invalid link for all 400 errors', async () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    regulatorUsersRegistrationService.acceptRegulatorInvitation.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'testCode' }, status: 400 })),
    );

    await expect(
      lastValueFrom(getGuard(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeFalsy();

    expect(navigateSpy).toHaveBeenCalledWith(['invitation/regulator/invalid-link'], {
      queryParams: { code: 'testCode' },
      replaceUrl: true,
    });
  });

  it('should resolved the invited user and return true when invitation status is from pending to enable', async () => {
    const invitedUser: InvitedUserInfoDTO = {
      email: 'user@pmrv.uk',
      invitationStatus: 'ALREADY_REGISTERED_SET_PASSWORD_ONLY',
    };

    const route = new ActivatedRouteSnapshotStub(undefined, { token: 'token' });
    regulatorUsersRegistrationService.acceptRegulatorInvitation.mockReturnValue(of(invitedUser));

    await lastValueFrom(getGuard(route));

    expect(store.state).toEqual(invitedUser);
    expect(regulatorUsersRegistrationService.acceptRegulatorInvitation).toHaveBeenCalledWith({
      token: 'token',
    });
  });

  it('should resolved the invited user and navigate to confirmed when invitation status is already registered', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const invitedUser: InvitedUserInfoDTO = { email: 'user@pmrv.uk', invitationStatus: 'ALREADY_REGISTERED' };
    const route = new ActivatedRouteSnapshotStub(undefined, { token: 'token' });

    regulatorUsersRegistrationService.acceptRegulatorInvitation.mockReturnValue(of(invitedUser));

    await lastValueFrom(getGuard(route));

    expect(store.state).toEqual(invitedUser);
    expect(regulatorUsersRegistrationService.acceptRegulatorInvitation).toHaveBeenCalledWith({
      token: 'token',
    });

    expect(navigateSpy).toHaveBeenCalledWith(['invitation/regulator/confirmed'], { replaceUrl: true });
  });
});
