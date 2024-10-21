import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import {
  InvitedSectorUserExtended,
  SectorUserInvitationStore,
} from '../sector-user-invitation/sector-user-invitation.store';
import { InvitationExistingConfirmationComponent } from './invitation-existing-confirmation.component';

describe('InvitationExistingConfirmationComponent', () => {
  let component: InvitationExistingConfirmationComponent;
  let fixture: ComponentFixture<InvitationExistingConfirmationComponent>;
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
      imports: [InvitationExistingConfirmationComponent],
      providers: [SectorUserInvitationStore, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    sectorUserInvitationStore = TestBed.inject(SectorUserInvitationStore);
    sectorUserInvitationStore.setState(sectorUserStoreState);

    fixture = TestBed.createComponent(InvitationExistingConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
