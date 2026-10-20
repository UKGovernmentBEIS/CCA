import { Directive, DOCUMENT, HostListener, inject } from '@angular/core';
import { Router } from '@angular/router';

@Directive({ selector: 'router-outlet[ccaSkipLinkFocus]' })
export class SkipLinkFocusDirective {
  private readonly router = inject(Router);
  private readonly document = inject(DOCUMENT);

  @HostListener('activate')
  onRouteActivation(): void {
    if (this.router.currentNavigation()?.trigger !== 'popstate') {
      const target = this.document.querySelector('govuk-skip-link') as HTMLAnchorElement;
      target.tabIndex = 0;
      target.focus({ preventScroll: true });
      target.removeAttribute('tabIndex');
    }
  }
}
