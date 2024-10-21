import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { InvitedSectorUserExtended, SectorUserInvitationStore } from '../sector-user-invitation.store';
import { SectorUserInvitationSummaryComponent } from './sector-user-invitation-summary.component';

describe('SectorUserInvitationSummaryComponent', () => {
  let component: SectorUserInvitationSummaryComponent;
  let fixture: ComponentFixture<SectorUserInvitationSummaryComponent>;
  let sectorUserInvitationStore: SectorUserInvitationStore;

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
      imports: [SectorUserInvitationSummaryComponent],
      providers: [
        SectorUserInvitationStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    sectorUserInvitationStore = TestBed.inject(SectorUserInvitationStore);
    sectorUserInvitationStore.setState(sectorUserStoreState);

    fixture = TestBed.createComponent(SectorUserInvitationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render "details" section with correct data', () => {
    const detailsList = document.querySelectorAll("[data-testid='sector-user-invitation-details-list'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent.trim(), div.querySelector('dd').textContent.trim()]);
    });

    expect(elements).toEqual([
      ['First name', 'name'],
      ['Last name', 'surname'],
      ['Job title', 'job'],
      ['Email address', 'test@example.com'],
    ]);
  });

  it('should render "organisation details" section with correct data', () => {
    const detailsList = document.querySelectorAll(
      "[data-testid='sector-user-invitation-organisation-details-list'] div",
    );

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent.trim(), div.querySelector('dd').textContent.trim()]);
    });

    expect(elements).toEqual([
      ['Contact type', 'Consultant'],
      ['Organisation name', 'organisation'],
      ['Phone number 1', 'UK (44) 1234567890'],
      ['Phone number 2', ''],
    ]);
  });

  it('should render "password" section with no visible data', () => {
    const detailsList = document.querySelectorAll("[data-testid='sector-user-invitation-password'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent.trim(), div.querySelector('dd').textContent.trim()]);
    });

    expect(elements).toEqual([['Password', '']]);
  });
});
