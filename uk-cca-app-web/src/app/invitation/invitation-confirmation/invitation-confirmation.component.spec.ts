import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { mockClass } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { InvitationConfirmationComponent } from './invitation-confirmation.component';

describe('InvitationConfirmationComponent', () => {
  let component: InvitationConfirmationComponent;
  let fixture: ComponentFixture<InvitationConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitationConfirmationComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: mockClass(AuthService) }],
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
