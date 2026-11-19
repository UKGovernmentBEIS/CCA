import { TestBed } from '@angular/core/testing';

import { PasswordStrengthMeterService, resolveZxcvbn } from './password-strength-meter.service';

describe('PasswordStrengthMeterService', () => {
  let service: PasswordStrengthMeterService;

  const weakPassword = 'aaaaaaaaaaaa';
  const strongPassword = 'Tr0ub4dor&3xample!Kx9';

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [PasswordStrengthMeterService] });
    service = TestBed.inject(PasswordStrengthMeterService);
  });

  it('should keep v4 scoring parity for known passwords', () => {
    expect(service.score(weakPassword)).toBe(0);
    expect(service.score('123456abc12345')).toBe(3);
    expect(service.score(strongPassword)).toBe(4);
  });

  it('should return translated feedback for a repeated-character password', () => {
    const { feedback } = service.scoreWithFeedback(weakPassword);

    expect(feedback?.warning).toBe('Repeated characters like "aaa" are easy to guess.');
    expect(feedback?.warning).not.toBe('simpleRepeat');
    expect(feedback?.suggestions).toContain('Avoid repeated words and characters.');
    expect(feedback?.suggestions).toContain('Add more words that are less common.');
  });

  it('should return keyboard-pattern feedback when adjacency graphs are configured', () => {
    const { feedback } = service.scoreWithFeedback('qwertyuiop');

    expect(feedback?.warning).toBe('Straight rows of keys on your keyboard are easy to guess.');
    expect(feedback?.suggestions).toContain('Use longer keyboard patterns and change typing direction multiple times.');
  });

  it('should return translated feedback for a short keyboard pattern', () => {
    const { feedback } = service.scoreWithFeedback('1qazxsw2');

    expect(feedback?.warning).toBe('Short keyboard patterns are easy to guess.');
    expect(feedback?.suggestions).toContain('Use longer keyboard patterns and change typing direction multiple times.');
  });

  it('should keep the default graphs and translations when a config is injected', () => {
    const zxcvbn = resolveZxcvbn({ maxLength: 64 });

    const { feedback } = zxcvbn.check('qwertyuiop');

    expect(feedback?.warning).toBe('Straight rows of keys on your keyboard are easy to guess.');
  });
});
