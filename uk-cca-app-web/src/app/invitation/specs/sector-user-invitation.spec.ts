import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { mockClass } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { PasswordService } from '../../shared/components/password/password.service';
import { INVITATION_ROUTES } from '../invitation.routes';
import {
  InvitedSectorUserExtended,
  SectorUserInvitationStore,
} from '../sector-user-invitation/sector-user-invitation.store';
import {
  checkUserDetailsAndSubmit,
  fillPassword,
  navigateAndFillSectorUserInvitationDetails,
  navigateToExistingSectorUserInvitationConfirmation,
  navigateToExpiredOrInvalidLink,
} from './test.utils';

describe('Sector User Invitation Spec', () => {
  let httpTestingController: HttpTestingController;
  let harness: RouterTestingHarness;
  let passwordService: Partial<jest.Mocked<PasswordService>>;
  let authService: Partial<jest.Mocked<AuthService>>;

  const sectorUserStoreState: InvitedSectorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'sector_user_basic_user',
    email: 'test@example.com',
    emailToken: 'aslfijmaslifhmsalf',
    invitationStatus: 'PENDING_TO_REGISTERED_SET_REGISTER_FORM',
    organisationName: 'organisation',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
    sector: 'sector',
  };

  beforeEach(async () => {
    passwordService = mockClass(PasswordService);
    passwordService.blacklisted.mockReturnValue(of(null));
    passwordService.strong.mockReturnValue(null);

    authService = mockClass(AuthService);

    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'invitation', children: INVITATION_ROUTES }]),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZxvbnServiceForPSM(),
        { provide: SectorUserInvitationStore, useValue: sectorUserStoreState },
        { provide: PasswordService, useValue: passwordService },
        { provide: AuthService, useValue: authService },
      ],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    await TestBed.compileComponents();
    harness = await RouterTestingHarness.create();
  });

  afterEach(fakeAsync(() => {
    flush();
    httpTestingController.verify();
  }));

  test('Main: new user activates her/his account', async () => {
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateAndFillSectorUserInvitationDetails(sectorUserStoreState, opts);
    await fillPassword('ThisIsAStrongP@ssw0rd', 'ThisIsAStrongP@ssw0rd', opts);
    await checkUserDetailsAndSubmit(sectorUserStoreState, opts);

    expect(screen.getByText(`You've successfully created a user account`)).toBeInTheDocument();
    expect(screen.getByText(`You can sign in to the CCA reporting service.`)).toBeInTheDocument();
  });

  test('Alternative scenario 1: Existing User activates her/his user account ', async () => {
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateToExistingSectorUserInvitationConfirmation(sectorUserStoreState, opts);

    expect(screen.getByText('You have been added as Basic user to the account of sector')).toBeInTheDocument();
  });

  test('Alternative scenario 2: User clicks on an email verification link which has expired', async () => {
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };
    const errorRes = {
      code: 'EMAIL1001',
      message: 'This link has expired',
      security: true,
      data: [[]],
    };

    await navigateToExpiredOrInvalidLink(sectorUserStoreState, errorRes, opts);

    expect(screen.getByText('This link has expired')).toBeInTheDocument();

    expect(
      screen.getByText(
        'Please contact the admin of your organisation and request that they add you once again as a new user.',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'When the admin user has done this you will receive a new email with a link enabling you to activate your account.',
      ),
    ).toBeInTheDocument();
  });

  test('Alternative scenario 3: User re-enters password which does not match', async () => {
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };

    await navigateAndFillSectorUserInvitationDetails(sectorUserStoreState, opts);
    await fillPassword('ThisIsAStrongP@ssw0rd', 'OrIsIt?', opts);

    expect(
      screen.getByText('Password and re-typed password do not match. Please enter both passwords again'),
    ).toBeInTheDocument();
  });

  test('Alternative scenario 4 and 5 (same frontend logic)', async () => {
    const user = UserEvent.setup();
    const opts = { harness, httpTestingController, user };
    const errorRes = {
      code: 'TOKEN1001',
      message: 'This link invalid',
      security: true,
      data: [[]],
    };

    await navigateToExpiredOrInvalidLink(sectorUserStoreState, errorRes, opts);

    expect(screen.getByText('This link is invalid')).toBeInTheDocument();
    expect(screen.getByText('Please contact your admin for access.')).toBeInTheDocument();
  });
});
