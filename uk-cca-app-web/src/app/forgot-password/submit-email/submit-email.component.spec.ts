import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { click, type } from '@testing';

import { ForgotPasswordService } from 'cca-api';

import { SubmitEmailComponent } from './submit-email.component';

describe('SubmitEmailComponent', () => {
  let fixture: ComponentFixture<SubmitEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmitEmailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ForgotPasswordService,
          useValue: { sendResetPasswordEmail: jest.fn((email) => of(email)) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmitEmailComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.nativeElement.querySelector('[data-testid="submit-email"]')).toBeTruthy();
  });

  it('should accept valid email address', () => {
    const input = fixture.nativeElement.querySelector('input');
    const button = fixture.nativeElement.querySelector('button');

    type(input, 'test');
    click(button);
    fixture.detectChanges();

    expect(
      fixture.nativeElement.textContent.includes('Enter an email address in the correct format, like name@example.com'),
    ).toBe(true);

    type(input, 'test@test.com');
    click(button);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="email-sent"]')).toBeTruthy();
  });
});
