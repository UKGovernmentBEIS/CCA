import { inject, Service } from '@angular/core';

import { ZxcvbnFactory } from '@zxcvbn-ts/core';
import { adjacencyGraphs } from '@zxcvbn-ts/language-common';

import translations from './translations';
import { FeedbackResult, ZXCVBN_CONFIG, ZxcvbnConfigType } from './types';

export const DEFAULT_ZXVBN_CONFIG: ZxcvbnConfigType = {
  translations: translations,
  graphs: adjacencyGraphs,
};

export const defaultZxcvbn = new ZxcvbnFactory(DEFAULT_ZXVBN_CONFIG);

export function resolveZxcvbn(options: ZxcvbnConfigType | null): ZxcvbnFactory {
  // v4 options replace the defaults wholesale, so carry graphs/translations into injected configs.
  return options ? new ZxcvbnFactory({ ...DEFAULT_ZXVBN_CONFIG, ...options }) : defaultZxcvbn;
}

@Service({ autoProvided: false })
export class PasswordStrengthMeterService {
  readonly options = inject<ZxcvbnConfigType>(ZXCVBN_CONFIG, { optional: true });

  private readonly zxcvbn = resolveZxcvbn(this.options);

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
    const result = this.zxcvbn.check(password);
    return result.score;
  }

  /**
   * Return the password strength score with feedback messages
   * return type FeedbackResult
   *
   * @param password - Password
   */
  scoreWithFeedback(password: string): FeedbackResult {
    const result = this.zxcvbn.check(password);
    return { score: result.score, feedback: result.feedback };
  }
}
