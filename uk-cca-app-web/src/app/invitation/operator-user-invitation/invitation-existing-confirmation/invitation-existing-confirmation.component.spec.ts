import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { InvitationExistingConfirmationComponent } from './invitation-existing-confirmation.component';

describe('InvitationExistingConfirmationComponent', () => {
  let component: InvitationExistingConfirmationComponent;
  let fixture: ComponentFixture<InvitationExistingConfirmationComponent>;
  let operatorUserInvitationStore: OperatorUserInvitationStore;

  const route = new ActivatedRouteStub();

  const operatorUserStoreState: InvitedOperatorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'sector_user_basic_user',
    email: 'test@example.com',
    emailToken: 'aslfijmaslifhmsalf',
    accountName: 'ADS-T0001',
    organisationName: 'organisation',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitationExistingConfirmationComponent],
      providers: [OperatorUserInvitationStore, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    operatorUserInvitationStore = TestBed.inject(OperatorUserInvitationStore);
    operatorUserInvitationStore.setState(operatorUserStoreState);

    fixture = TestBed.createComponent(InvitationExistingConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
