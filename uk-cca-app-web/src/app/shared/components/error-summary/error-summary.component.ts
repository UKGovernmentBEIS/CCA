import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, input, viewChild } from '@angular/core';
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
export class ErrorSummaryComponent implements AfterViewInit {
  protected readonly errorSummaryInfo = input.required<ErrorSummaryInfo>();

  private readonly container = viewChild.required<ElementRef<HTMLElement>>('container');

  ngAfterViewInit(): void {
    const container = this.container().nativeElement;
    if (container.scrollIntoView) container.scrollIntoView();
    if (container.focus) container.focus();
  }
}
