import { Directive, DOCUMENT, HostListener, inject } from '@angular/core';
import { Router } from '@angular/router';

import { focusMainContent } from '@shared/utils';

@Directive({ selector: 'router-outlet[ccaMainContentFocus]' })
export class MainContentFocusDirective {
  private readonly router = inject(Router);
  private readonly document = inject(DOCUMENT);

  @HostListener('activate')
  onRouteActivation(): void {
    // Skip the initial navigation: on page load there is no previous
    // successful navigation yet, and focus must stay at the document start so
    // the skip link remains the first tab stop.
    if (this.router.lastSuccessfulNavigation() == null) {
      return;
    }
    // Skip back/forward: the browser restores the scroll position, moving
    // focus would fight that.
    if (this.router.currentNavigation()?.trigger !== 'popstate') {
      focusMainContent(this.document);
    }
  }
}
