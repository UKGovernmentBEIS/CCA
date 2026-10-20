import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { OperatorUserInvitationSummaryComponent } from './operator-user-invitation-summary.component';

describe('OperatorUserInvitationSummaryComponent', () => {
  let component: OperatorUserInvitationSummaryComponent;
  let fixture: ComponentFixture<OperatorUserInvitationSummaryComponent>;
  let operatorUserInvitationStore: OperatorUserInvitationStore;

  const route = new ActivatedRouteStub();

  const operatorUserStoreState: InvitedOperatorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'operator_user_basic_user',
    email: 'test@example.com',
    emailToken: 'aslfijmaslifhmsalf',
    organisationName: 'organisation',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorUserInvitationSummaryComponent],
      providers: [
        OperatorUserInvitationStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    operatorUserInvitationStore = TestBed.inject(OperatorUserInvitationStore);
    operatorUserInvitationStore.setState(operatorUserStoreState);

    fixture = TestBed.createComponent(OperatorUserInvitationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render "details" section with correct data', () => {
    const detailsList = document.querySelectorAll("[data-testid='operator-user-invitation-details-list'] div");

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
      "[data-testid='operator-user-invitation-organisation-details-list'] div",
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
    const detailsList = document.querySelectorAll("[data-testid='operator-user-invitation-password'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent.trim(), div.querySelector('dd').textContent.trim()]);
    });

    expect(elements).toEqual([['Password', '']]);
  });
});
