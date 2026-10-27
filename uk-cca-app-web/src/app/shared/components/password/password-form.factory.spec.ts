import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { ValidatePasswordService } from 'cca-api';

import { ZXCVBN_CONFIG, ZxcvbnConfigType } from '../password-strength-meter/types';
import { PASSWORD_FORM, passwordFormFactory } from './password-form.factory';

const strongPassword = 'Tr0ub4dor&3xample!Kx9';

const mockValidatePasswordService = { validatePassword: vi.fn().mockReturnValue(of(null)) };

describe('passwordFormFactory', () => {
  const createForm = (config?: ZxcvbnConfigType) => {
    TestBed.configureTestingModule({
      providers: [
        passwordFormFactory,
        { provide: ValidatePasswordService, useValue: mockValidatePasswordService },
        ...(config ? [{ provide: ZXCVBN_CONFIG, useValue: config }] : []),
      ],
    });

    return TestBed.inject(PASSWORD_FORM);
  };

  beforeEach(() => {
    mockValidatePasswordService.validatePassword.mockReturnValue(of(null));
  });

  it('should accept a strong password with the default zxcvbn config', () => {
    const form = createForm();

    form.controls.password.setValue(strongPassword);

    expect(form.controls.password.errors).toBeNull();
  });

  it('should apply an injected ZXCVBN_CONFIG in the strong password validator', () => {
    const scoringAboveDefault: ZxcvbnConfigType = {
      timeEstimationValues: {
        scoring: { 0: 1e30, 1: 1e31, 2: 1e32, 3: 1e33 },
        attackTime: {
          onlineThrottlingXPerHour: 100,
          onlineNoThrottlingXPerSecond: 10,
          offlineSlowHashingXPerSecond: 1e4,
          offlineFastHashingXPerSecond: 1e10,
        },
      },
    };
    const form = createForm(scoringAboveDefault);

    form.controls.password.setValue(strongPassword);

    expect(form.controls.password.errors).toEqual({ weakPassword: 'Enter a strong password' });
  });
});
