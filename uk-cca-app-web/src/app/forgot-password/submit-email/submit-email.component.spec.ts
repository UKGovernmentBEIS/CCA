import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Subject } from 'rxjs';

import { click, queryByTestId, queryByText, type } from '@testing';

import { ForgotPasswordService } from 'cca-api';

import { SubmitEmailComponent } from './submit-email.component';

describe('SubmitEmailComponent', () => {
  let fixture: ComponentFixture<SubmitEmailComponent>;
  let sendResetPasswordEmail: ReturnType<typeof vi.fn>;
  let response: Subject<void>;

  const submitEmail = async (email: string): Promise<void> => {
    type(fixture.nativeElement.querySelector('input'), email);
    click(fixture.nativeElement.querySelector('button'));
    await fixture.whenStable();
  };

  const queryEmailSent = () => queryByTestId('email-sent', fixture.nativeElement);
  const queryForm = () => queryByTestId('submit-email', fixture.nativeElement);

  beforeEach(async () => {
    response = new Subject<void>();
    sendResetPasswordEmail = vi.fn(() => response);

    await TestBed.configureTestingModule({
      imports: [SubmitEmailComponent],
      providers: [
        provideRouter([{ path: 'forgot-password', children: [] }]),
        { provide: ForgotPasswordService, useValue: { sendResetPasswordEmail } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmitEmailComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(queryForm()).toBeTruthy();
  });

  it('should show validation errors and not request the reset email for an invalid address', async () => {
    await submitEmail('test');

    expect(queryByText(/Enter an email address in the correct format/, fixture.nativeElement)).toBeTruthy();
    expect(sendResetPasswordEmail).not.toHaveBeenCalled();
  });

  it('should show the confirmation page once the request has completed', async () => {
    await submitEmail('test@test.com');

    expect(sendResetPasswordEmail).toHaveBeenCalledWith({ email: 'test@test.com' });
    expect(queryEmailSent()).toBeFalsy();

    response.next();
    await fixture.whenStable();

    expect(queryEmailSent()).toBeTruthy();
    expect(queryForm()).toBeFalsy();
  });

  it('should return to the form when another reset email is requested', async () => {
    await submitEmail('test@test.com');
    response.next();
    await fixture.whenStable();

    click(queryEmailSent().querySelector('a'));
    await fixture.whenStable();

    expect(queryForm()).toBeTruthy();
    expect(queryEmailSent()).toBeFalsy();
  });
});
