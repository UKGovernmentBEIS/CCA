import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthService } from '@core/services/auth.service';
import { mockClass } from '@netz/common/testing';

import { SectorUserInvitationStore } from '../sector-user-invitation/sector-user-invitation.store';
import { InvitationConfirmationComponent } from './invitation-confirmation.component';

describe('InvitationConfirmationComponent', () => {
  let component: InvitationConfirmationComponent;
  let fixture: ComponentFixture<InvitationConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitationConfirmationComponent],
      providers: [SectorUserInvitationStore, { provide: AuthService, useValue: mockClass(AuthService) }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvitationConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
