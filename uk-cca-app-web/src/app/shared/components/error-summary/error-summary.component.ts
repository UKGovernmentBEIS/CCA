import { afterRenderEffect, Component, ElementRef, input, viewChild } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

export type ErrorSummaryInfo = {
  message: string;
  link?: string;
  linkText?: string;
  queryParams?: Params;
};

@Component({
  selector: 'cca-error-summary',
  templateUrl: './error-summary.component.html',
  imports: [RouterLink],
})
export class ErrorSummaryComponent {
  protected readonly errorSummaryInfo = input.required<ErrorSummaryInfo>();

  private readonly container = viewChild.required<ElementRef<HTMLElement>>('container');

  constructor() {
    // Pages pass a new errorSummaryInfo on each failure, so refocus even when the summary is already mounted.
    afterRenderEffect({
      write: () => {
        this.errorSummaryInfo();
        const container = this.container().nativeElement;
        if (container.scrollIntoView) container.scrollIntoView();
        if (container.focus) container.focus();
      },
    });
  }
}
