import { inject, Injectable } from '@angular/core';

import { zxcvbn, zxcvbnOptions } from '@zxcvbn-ts/core';
import { translations } from '@zxcvbn-ts/language-en';

import { FeedbackResult, ZXCVBN_CONFIG, ZxcvbnConfigType } from './types';

export const DEFAULT_ZXVBN_CONFIG: ZxcvbnConfigType = {
  translations: translations,
};

@Injectable()
export class PasswordStrengthMeterService {
  readonly options = inject<ZxcvbnConfigType>(ZXCVBN_CONFIG, { optional: true });

  constructor() {
    if (this.options) {
      zxcvbnOptions.setOptions(this.options);
    } else {
      zxcvbnOptions.setOptions(DEFAULT_ZXVBN_CONFIG);
    }
  }

  /**
   *  Return the password strength score in number
   *  0 - too guessable
   *  1 - very guessable
   *  2 - somewhat guessable
   *  3 - safely unguessable
   *  4 - very unguessable
   *
   *  @param password - Password
   */
  score(password: string): number {
    const result = zxcvbn(password);
    return result.score;
  }

  /**
   * Return the password strength score with feedback messages
   * return type FeedbackResult
   *
   * @param password - Password
   */
  scoreWithFeedback(password: string): FeedbackResult {
    const result = zxcvbn(password);
    return { score: result.score, feedback: result.feedback };
  }
}
