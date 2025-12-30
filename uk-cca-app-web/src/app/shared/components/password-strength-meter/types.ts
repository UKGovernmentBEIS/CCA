import { InjectionToken } from '@angular/core';

import { OptionsType } from '@zxcvbn-ts/core';

export type ZxcvbnConfigType = OptionsType;

export const ZXCVBN_CONFIG = new InjectionToken<ZxcvbnConfigType>('ZXCVBN_CONFIG');

export interface Feedback {
  warning: string | null;
  suggestions: string[];
}

export interface FeedbackResult {
  score: number | null;
  feedback: Feedback | null;
}
