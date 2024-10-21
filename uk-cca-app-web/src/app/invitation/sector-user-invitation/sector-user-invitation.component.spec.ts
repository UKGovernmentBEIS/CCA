import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorUserInvitationComponent } from './sector-user-invitation.component';
import { InvitedSectorUserExtended, SectorUserInvitationStore } from './sector-user-invitation.store';

describe('SectorUserInvitationComponent', () => {
  let component: SectorUserInvitationComponent;
  let fixture: ComponentFixture<SectorUserInvitationComponent>;
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
      imports: [SectorUserInvitationComponent],
      providers: [SectorUserInvitationStore, provideHttpClient(), { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    sectorUserInvitationStore = TestBed.inject(SectorUserInvitationStore);
    sectorUserInvitationStore.setState(sectorUserStoreState);

    fixture = TestBed.createComponent(SectorUserInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate the form with valid information', () => {
    expect(component.form.get('firstName').value).toEqual('name');
    expect(component.form.get('lastName').value).toEqual('surname');
    expect(component.form.get('email').value).toEqual('test@example.com');
    expect(component.form.get('contactType').value).toEqual('CONSULTANT');
  });
});
