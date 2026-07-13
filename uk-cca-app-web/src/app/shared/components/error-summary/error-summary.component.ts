import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

export type ErrorSummaryInfo = {
  message: string;
  link?: string;
  linkText?: string;
};

@Component({
  selector: 'cca-error-summary',
  templateUrl: './error-summary.component.html',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorSummaryComponent {
  protected readonly errorSummaryInfo = input.required<ErrorSummaryInfo>();
}
