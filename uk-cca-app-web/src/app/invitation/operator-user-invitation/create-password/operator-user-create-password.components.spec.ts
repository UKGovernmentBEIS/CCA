import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByLabelText, getByText, type } from '@testing';

import { ValidatePasswordService } from 'cca-api';

import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { OperatorUserCreatePasswordComponent } from './operator-user-create-password.component';

describe('OperatorUserCreatePasswordComponent', () => {
  let component: OperatorUserCreatePasswordComponent;
  let fixture: ComponentFixture<OperatorUserCreatePasswordComponent>;
  let operatorUserInvitationStore: OperatorUserInvitationStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const operatorUserStoreState: InvitedOperatorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'sector_user_basic_user',
    email: 'test@example.com',
    emailToken: 'aslfijmaslifhmsalf',
    organisationName: 'organisation',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  };

  const mockValidatePasswordService = { validatePassword: vi.fn().mockReturnValue(of(null)) };

  afterEach(() => {
    vi.restoreAllMocks();
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorUserCreatePasswordComponent],
      providers: [
        OperatorUserInvitationStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
        { provide: ValidatePasswordService, useValue: mockValidatePasswordService },
      ],
    }).compileComponents();

    operatorUserInvitationStore = TestBed.inject(OperatorUserInvitationStore);
    operatorUserInvitationStore.setState(operatorUserStoreState);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(OperatorUserCreatePasswordComponent);
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
          HTMLInputElement | HTMLSelectElement | null
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
    await fixture.whenStable();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
  });

  it('should submit the form', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    const stateSpy = vi.spyOn(operatorUserInvitationStore, 'updateState');

    type(
      getByLabelText('Create a password to activate your account', fixture.nativeElement) as HTMLInputElement,
      'ThisIsAStrongP@ssw0rd',
    );

    type(getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement, 'ThisIsAStrongP@ssw0rd');

    click(getByText('Continue', fixture.nativeElement));

    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(stateSpy).toHaveBeenCalledTimes(1);
  });
});
