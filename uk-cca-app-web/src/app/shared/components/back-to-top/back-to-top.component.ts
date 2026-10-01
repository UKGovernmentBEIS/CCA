import { ChangeDetectionStrategy, Component, DOCUMENT, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { focusMainContent } from '@shared/utils';

@Component({
  selector: 'cca-back-to-top',
  template: `
    <a class="govuk-link govuk-link--no-visited-state" [routerLink]="[]" (click)="scrollToTop()">
      <svg
        role="presentation"
        focusable="false"
        class="back-to-top__icon"
        xmlns="http://www.w3.org/2000/svg"
        width="13"
        height="17"
        viewBox="0 0 13 17"
      >
        <path fill="currentColor" d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"></path>
      </svg>
      Back to top
    </a>
  `,
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackToTopComponent {
  private readonly document = inject(DOCUMENT);

  scrollToTop() {
    // Move keyboard focus first: with `preventScroll` the focus cannot disturb
    // the smooth scroll, and the focused link no longer scrolls out of view
    // (ARIA 1.2 section 4.3.1).
    focusMainContent(this.document);
    this.document.defaultView?.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth',
    });
  }
}
