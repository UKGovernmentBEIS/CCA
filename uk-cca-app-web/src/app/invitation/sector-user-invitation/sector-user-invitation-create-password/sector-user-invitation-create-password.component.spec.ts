import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getAllByText, getByLabelText, getByText, type } from '@testing';

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
    expect(
      (
        getByLabelText('Create a password to activate your account', fixture.nativeElement) as
          | HTMLInputElement
          | HTMLSelectElement
          | null
      )?.value ?? '',
    ).toBe('');
    expect(
      (getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('');
  });

  it('should show form errors', async () => {
    type(
      getByLabelText('Create a password to activate your account', fixture.nativeElement) as HTMLInputElement,
      '123',
    );
    type(getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement, '456');

    click(getByText('Continue', fixture.nativeElement));
    fixture.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();

    expect(
      getByText(
        'Password and re-typed password do not match. Please enter both passwords again',
        fixture.nativeElement,
      ),
    ).toBeTruthy();

    expect(
      getByText(
        'Your password must be 12 characters or longer and can include letters, numbers and symbols or a combination of three random words.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();

    expect(getAllByText('Password must be 12 characters or more', fixture.nativeElement)).toHaveLength(2);
    expect(getAllByText('Enter a strong password', fixture.nativeElement)).toHaveLength(2);
  });

  it('should submit the form', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    type(
      getByLabelText('Create a password to activate your account', fixture.nativeElement) as HTMLInputElement,
      'ThisIsAStrongP@ssw0rd',
    );
    type(getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement, 'ThisIsAStrongP@ssw0rd');

    click(getByText('Continue', fixture.nativeElement));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });
});
