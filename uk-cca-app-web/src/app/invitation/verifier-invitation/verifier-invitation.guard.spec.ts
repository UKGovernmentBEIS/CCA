import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { InvitedUserInfoDTO, VerifierUsersRegistrationService } from 'cca-api';

import { invitedUser, VerifierInvitationGuard } from './verifier-invitation.guard';

describe('VerifierInvitationGuard', () => {
  let router: Router;
  let verifierUsersRegistrationService: jest.Mocked<VerifierUsersRegistrationService>;

  beforeEach(() => {
    verifierUsersRegistrationService = mockClass(VerifierUsersRegistrationService);
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: VerifierUsersRegistrationService, useValue: verifierUsersRegistrationService }],
    });
    router = TestBed.inject(Router);
  });
  function getGuard(route: ActivatedRouteSnapshot) {
    return TestBed.runInInjectionContext(() => VerifierInvitationGuard(route));
  }

  it('should navigate to root if there is no token query param', async () => {
    await expect(lastValueFrom(getGuard(new ActivatedRouteSnapshotStub()))).resolves.toEqual(
      router.parseUrl('landing'),
    );
    expect(verifierUsersRegistrationService.acceptVerifierInvitation).not.toHaveBeenCalled();
  });

  it('should navigate to invalid link for all 400 errors', async () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    verifierUsersRegistrationService.acceptVerifierInvitation.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'testCode' }, status: 400 })),
    );

    await expect(
      lastValueFrom(getGuard(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['invitation/verifier/invalid-link'], {
      queryParams: { code: 'testCode' },
    });
  });

  it('should resolved the invited user', async () => {
    const _invitedUser: InvitedUserInfoDTO = { email: 'user@cca.uk' };
    verifierUsersRegistrationService.acceptVerifierInvitation.mockReturnValue(of(_invitedUser));

    await lastValueFrom(getGuard(new ActivatedRouteSnapshotStub(undefined, { token: 'token' })));

    expect(invitedUser).toEqual(_invitedUser);
    expect(verifierUsersRegistrationService.acceptVerifierInvitation).toHaveBeenCalledWith({
      token: 'token',
    });
  });
});
