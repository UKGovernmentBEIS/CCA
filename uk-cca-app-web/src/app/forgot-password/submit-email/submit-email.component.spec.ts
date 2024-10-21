import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { ForgotPasswordService } from 'cca-api';

import { SubmitEmailComponent } from './submit-email.component';

describe('SubmitEmailComponent', () => {
  beforeEach(async () => {
    await render(SubmitEmailComponent, {
      providers: [
        provideRouter([]),
        {
          provide: ForgotPasswordService,
          useValue: { sendResetPasswordEmail: jest.fn((email) => of(email)) },
        },
      ],
    });
  });

  it('should create', () => {
    expect(screen.getByTestId('submit-email')).toBeTruthy();
  });

  it('should accept valid email address', async () => {
    const user = UserEvent.setup();
    await user.type(document.querySelector('input'), 'test');
    await user.click(screen.getByRole('button'));
    expect(screen.getByText('Enter an email address in the correct format, like name@example.com')).toBeVisible();
    await user.type(document.querySelector('input'), 'test@test.com');
    await user.click(screen.getByRole('button'));
    expect(screen.getByTestId('email-sent')).toBeVisible();
  });
});
