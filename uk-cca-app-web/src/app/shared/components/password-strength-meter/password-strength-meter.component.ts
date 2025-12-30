import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output, signal } from '@angular/core';

import { PasswordStrengthMeterService } from './password-strength-meter.service';
import { ProgressBarDirective } from './progress-bar.directive';
import { Feedback, FeedbackResult } from './types';

@Component({
  selector: 'cca-password-strength-meter',
  templateUrl: './password-strength-meter.component.html',
  imports: [ProgressBarDirective],
  providers: [PasswordStrengthMeterService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PasswordStrengthMeterComponent {
  private readonly passwordStrengthMeterService = inject(PasswordStrengthMeterService);

  protected readonly password = input<string | null>(null);
  protected readonly minPasswordLength = input(8);
  protected readonly enableFeedback = input(false);
  protected readonly colors = input(['#d4351c', '#d4351c', '#f47738', '#00703c', '#00703c']);
  protected readonly numberOfProgressBarItems = input(5);

  private readonly prevPasswordStrength = signal<number | null>(null);

  protected readonly strengthChange = output<number | null>();

  protected readonly passwordStrength = computed<number | null>(() => {
    if (!this.password()) return null;
    if (this.password().length < this.minPasswordLength()) return 0;
    return this.calculateScore(this.password()).score;
  });

  protected readonly feedback = computed<Feedback | null>(() => {
    if (!this.password()) return null;
    if (this.password().length < this.minPasswordLength()) return null;
    return this.calculateScore(this.password()).feedback;
  });

  constructor() {
    effect(() => {
      if (this.prevPasswordStrength() !== this.passwordStrength()) {
        this.strengthChange.emit(this.passwordStrength());
        this.prevPasswordStrength.set(this.passwordStrength());
      }
    });
  }

  private calculateScore(password: string): FeedbackResult {
    if (this.enableFeedback()) {
      return this.passwordStrengthMeterService.scoreWithFeedback(password);
    }

    const feedbackResult = {
      score: this.passwordStrengthMeterService.score(password),
      feedback: null,
    };

    return feedbackResult;
  }
}
