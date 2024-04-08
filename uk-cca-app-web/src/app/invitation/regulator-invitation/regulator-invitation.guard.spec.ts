import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { InvitedUserInfoDTO, RegulatorUsersRegistrationService } from 'cca-api';

import { invitedUser, RegulatorInvitationGuard } from './regulator-invitation.guard';

describe('RegulatorInvitationGuard', () => {
  let router: Router;
  let regulatorUsersRegistrationService: jest.Mocked<RegulatorUsersRegistrationService>;

  beforeEach(() => {
    regulatorUsersRegistrationService = mockClass(RegulatorUsersRegistrationService);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: RegulatorUsersRegistrationService, useValue: regulatorUsersRegistrationService }],
    });

    router = TestBed.inject(Router);
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
    });
  });

  it('should resolved the invited user', async () => {
    const _invitedUser: InvitedUserInfoDTO = { email: 'user@cca.uk' };
    regulatorUsersRegistrationService.acceptRegulatorInvitation.mockReturnValue(of(_invitedUser));

    await lastValueFrom(getGuard(new ActivatedRouteSnapshotStub(undefined, { token: 'token' })));

    expect(invitedUser?.email).toEqual(_invitedUser.email);
    expect(regulatorUsersRegistrationService.acceptRegulatorInvitation).toHaveBeenCalledWith({
      token: 'token',
    });
  });
});
