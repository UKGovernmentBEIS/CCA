import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorUserInvitationComponent } from './sector-user-invitation.component';
import { InvitedSectorUserExtended, SectorUserInvitationStore } from './sector-user-invitation.store';

describe('SectorUserInvitationComponent', () => {
  let component: SectorUserInvitationComponent;
  let fixture: ComponentFixture<SectorUserInvitationComponent>;
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
      imports: [SectorUserInvitationComponent],
      providers: [SectorUserInvitationStore, provideHttpClient(), { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    router = TestBed.inject(Router);
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
  it('should show errors when required fields are missing', () => {
    component.form.get('firstName').setValue('');
    component.form.get('lastName').setValue('');
    fixture.detectChanges();

    component.onSubmitSectorUserInvitationDetails();
    fixture.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(document.body.textContent).toMatch(/first name/i);
    expect(document.body.textContent).toMatch(/last name/i);
  });

  it('should submit the form and update state when valid', () => {
    component.form.get('firstName').setValue('Valid');
    component.form.get('lastName').setValue('User');
    fixture.detectChanges();

    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onSubmitSectorUserInvitationDetails();
    fixture.detectChanges();

    expect(component.form.valid).toBe(true);
    expect(navigateSpy).toHaveBeenCalled();
  });
});
