import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { AddConfirmationComponent } from './add-confirmation.component';

describe('AddRegulatorConfirmationComponent', () => {
  const email = 'regulator_1245@cca.uk';

  beforeEach(async () => {
    await render(AddConfirmationComponent, {
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, { email }),
        },
      ],
    });
  });

  it('should render', () => {
    expect(screen.getByTestId('confirmation-screen')).toBeInTheDocument();
    const text = `An account confirmation email has been sent to ${email}`;
    expect(screen.getByText(text)).toBeInTheDocument();
  });
});
