import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { ActivatedRouteStub } from '@netz/common/testing';
import { BackToTopComponent } from '@shared/components';
import { screen } from '@testing-library/dom';
import { KeycloakService } from 'keycloak-angular';

import { AuthoritiesService, TermsAndConditionsService, UsersService, UserStateDTO } from 'cca-api';

import {
  mockAuthorityService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from '../shared/guards/mocks';
import { LandingPageComponent } from './landing-page.component';

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  let authStore: AuthStore;

  const route = new ActivatedRouteStub();

  const setUser = (roleType: UserStateDTO['roleType'], loginStatuses: UserStateDTO['status']) => {
    authStore.setUserState({ roleType, status: loginStatuses });
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingPageComponent, PageHeadingComponent, BackToTopComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(false);

    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the landing page buttons if not logged in', () => {
    const organisationLink = document.querySelector('a[href="/accounts/new"]');
    const notLoggedInLandingPageLinks = document.querySelectorAll('.govuk-button--start');

    expect(organisationLink).toBeFalsy();
    expect(notLoggedInLandingPageLinks).toHaveLength(1);
  });

  it('should only display application button to operators', async () => {
    const notLoggedInLandingPageLinks = document.querySelectorAll('.govuk-button--start');

    expect(notLoggedInLandingPageLinks).toHaveLength(1);

    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', 'NO_AUTHORITY');
    expect(screen.getByText('Contact your administrator to access your account.')).toBeInTheDocument();

    setUser('REGULATOR', 'DISABLED');
    expect(
      screen.getByText(
        'Your user account has been disabled. Please contact your admin to gain access to your account.',
      ),
    ).toBeInTheDocument();
  });

  it(`should show disabled message when role='REGULATOR' and status 'DISABLED'`, () => {
    authStore.setIsLoggedIn(true);
    setUser('REGULATOR', 'DISABLED');
    expect(
      screen.getByText(
        'Your user account has been disabled. Please contact your admin to gain access to your account.',
      ),
    ).toBeInTheDocument();
  });

  it(`should show ACCEPTED message when user login status is 'ACCEPTED'`, () => {
    authStore.setIsLoggedIn(true);
    setUser('SECTOR_USER', 'ACCEPTED');
    expect(screen.getByText('Your user account needs activation.')).toBeInTheDocument();
    expect(screen.getByText('Contact your admin to gain access to your account.')).toBeInTheDocument();
  });
});
