import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';

import { RoleCode, roleOptions } from '../../types';
import { AddSectorConfirmationComponent } from './confirmation.component';

describe('AddSectorConfirmationComponent', () => {
  let fixture: ComponentFixture<AddSectorConfirmationComponent>;
  const role: RoleCode = 'sector_user_administrator';
  const email = 'sector_user@cca.uk';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddSectorConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub({}, { role, email }) }],
    }).compileComponents();

    fixture = TestBed.createComponent(AddSectorConfirmationComponent);

    fixture.detectChanges();
  });

  it('should render confirmation banner with the appropriate email', () => {
    const text = `An account confirmation email has been sent to ${email}`;
    expect(getByTestId('confirmation-screen')).toBeTruthy();
    expect(getByText(text)).toBeTruthy();
  });

  it('should render confirmation banner with the role', () => {
    const roleText = roleOptions.find((r) => r.value === role)?.text;
    const roleContents = `The new ${roleText.toLowerCase()} will be able to sign in to the service once they confirm their account.`;
    expect(getByText(roleContents)).toBeTruthy();
  });

  it('should render `return to: Contacts` link', () => {
    expect(getByText('Return to: Contacts')).toBeTruthy();
  });
});
