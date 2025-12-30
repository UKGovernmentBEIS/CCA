import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { InvitedSectorUserExtended, SectorUserInvitationStore } from '../sector-user-invitation.store';
import { SectorUserInvitationCreatePasswordComponent } from './sector-user-invitation-create-password.component';

describe('SectorUserInvitationCreatePasswordComponent', () => {
  let component: SectorUserInvitationCreatePasswordComponent;
  let fixture: ComponentFixture<SectorUserInvitationCreatePasswordComponent>;
  let sectorUserInvitationStore: SectorUserInvitationStore;
  let router: Router;

  const route = new ActivatedRouteStub();

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
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorUserInvitationCreatePasswordComponent],
      providers: [
        SectorUserInvitationStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    sectorUserInvitationStore = TestBed.inject(SectorUserInvitationStore);
    sectorUserInvitationStore.setState(sectorUserStoreState);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(SectorUserInvitationCreatePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the form with no password information', () => {
    expect(screen.getByLabelText('Create a password to activate your account')).toHaveValue('');
    expect(screen.getByLabelText('Re-enter your password')).toHaveValue('');
  });

  it('should show form errors', async () => {
    const user = UserEvent.setup();
    await user.type(screen.getByLabelText('Create a password to activate your account'), '123');
    await user.type(screen.getByLabelText('Re-enter your password'), '456');

    await user.click(screen.getByText('Continue'));
    fixture.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();

    expect(
      screen.getByText('Password and re-typed password do not match. Please enter both passwords again'),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'Your password must be 12 characters or longer and can include letters, numbers and symbols or a combination of three random words.',
      ),
    ).toBeInTheDocument();

    expect(screen.getAllByText('Password must be 12 characters or more')).toHaveLength(2);
    expect(screen.getAllByText('Enter a strong password')).toHaveLength(2);
  });

  it('should submit the form', async () => {
    const user = UserEvent.setup();
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    await user.type(screen.getByLabelText('Create a password to activate your account'), 'ThisIsAStrongP@ssw0rd');
    await user.type(screen.getByLabelText('Re-enter your password'), 'ThisIsAStrongP@ssw0rd');

    await user.click(screen.getByText('Continue'));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });
});
