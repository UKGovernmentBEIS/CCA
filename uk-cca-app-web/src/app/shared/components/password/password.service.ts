import { AbstractControl } from '@angular/forms';

import { debounceTime, from, map, Observable, switchMap } from 'rxjs';

import { MessageValidationErrors } from '@netz/govuk-components';
import { generateSHA1String } from '@shared/utils';
import { zxcvbn } from '@zxcvbn-ts/core';

const isBlacklistedPassword = (password: string): Observable<boolean> => {
  return from(generateSHA1String(password)).pipe(
    map((s) => ({ hexString: s, prefix: s.substring(0, 5) })),
    switchMap(({ hexString, prefix }) =>
      from(
        fetch(`https://api.pwnedpasswords.com/range/${prefix}`, {
          headers: { 'Content-Type': 'text/plain' },
        }).then((r) => r.text()),
      ).pipe(
        map((v) => ({
          v,
          hexString,
        })),
      ),
    ),
    map(({ hexString, v }) => v.indexOf(hexString.substring(5).toUpperCase()) >= 0),
  );
};

const blacklisted = (control: AbstractControl): Observable<MessageValidationErrors | null> => {
  return isBlacklistedPassword(control.value).pipe(
    debounceTime(500),
    map((isBlacklisted: boolean) =>
      isBlacklisted ? { blacklisted: 'Password has been blacklisted. Select another password.' } : null,
    ),
  );
};

const strong = (control: AbstractControl): MessageValidationErrors | null => {
  const strength = zxcvbn(control.value ?? '').score;

  return strength > 2 ? null : { weakPassword: 'Enter a strong password' };
};

export const PasswordService = { blacklisted, strong };
