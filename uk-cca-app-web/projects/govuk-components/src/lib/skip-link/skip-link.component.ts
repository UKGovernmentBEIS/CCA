import { AsyncPipe, DOCUMENT } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';

import { filter, map, tap } from 'rxjs';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'govuk-skip-link',
  standalone: true,
  imports: [RouterLink, AsyncPipe],
  template: `
    <div>
      <a class="govuk-skip-link" [routerLink]="routerLink | async" queryParamsHandling="preserve" [fragment]="anchor"
        >Skip to main content</a
      >
    </div>
  `,
  styles: `
    div {
      float: left;
      margin: 0.3em 0 0 0.3em;
      z-index: 1;
      position: absolute;
    }
  `,
})
export class SkipLinkComponent {
  @Input() anchor = 'main-content';
  document = inject(DOCUMENT);
  route = inject(ActivatedRoute);
  router = inject(Router);

  routerLink = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map((event: NavigationEnd) => event.url.split('#')[0].split('?')[0]),
  );

  constructor() {
    this.route.fragment
      .pipe(
        tap((f) => {
          if (f === this.anchor) {
            this.document.getElementById(this.anchor).focus();
          }
        }),
        takeUntilDestroyed(),
      )
      .subscribe();
  }
}
