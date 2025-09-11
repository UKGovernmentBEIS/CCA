import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { RoleCode, roleOptions } from '../../types';
import { AddSectorConfirmationComponent } from './confirmation.component';

describe('AddSectorConfirmationComponent', () => {
  const role: RoleCode = 'sector_user_administrator';
  const email = 'sector_user@cca.uk';

  beforeEach(async () => {
    await render(AddSectorConfirmationComponent, {
      configureTestBed: (testbed) => {
        const route = new ActivatedRouteStub({}, { role, email });
        testbed.overrideProvider(ActivatedRoute, { useValue: route });
      },
    });
  });

  it('should render confirmation banner with the appropriate email', () => {
    const text = `An account confirmation email has been sent to ${email}`;
    expect(screen.getByTestId('confirmation-screen')).toBeInTheDocument();
    expect(screen.getByText(text)).toBeInTheDocument();
  });

  it('should render confirmation banner with the role', () => {
    const roleText = roleOptions.find((r) => r.value === role)?.text;
    const roleContents = `The new ${roleText.toLowerCase()} will be able to sign in to the service once they confirm their account.`;
    expect(screen.getByText(roleContents)).toBeInTheDocument();
  });

  it('should render `return to: Contacts` link', () => {
    expect(screen.getByText('Return to: Contacts')).toBeInTheDocument();
  });
});
