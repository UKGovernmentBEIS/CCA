import { Component, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';

import { filter, map } from 'rxjs';

import { BackLinkComponent as GovukBackLinkComponent } from '@netz/govuk-components';

import { resolveBacklink } from './resolve-backlink';

@Component({
  selector: 'netz-back-link',
  template: `
    @if (backlink(); as backlink) {
      <govuk-back-link
        [link]="backlink.link"
        [fragment]="backlink.fragment!"
        [queryParams]="backlink.queryParams!"
        [route]="backlink.route"
        [inverse]="inverse()"
      />
    }
  `,
  imports: [GovukBackLinkComponent],
})
export class BackLinkComponent {
  private readonly router = inject(Router);

  readonly inverse = input(false);

  protected readonly backlink = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => {
        let activeRoute = this.router.routerState.snapshot.root;
        while (activeRoute.firstChild) {
          activeRoute = activeRoute.firstChild;
        }

        const backlink = resolveBacklink(activeRoute);
        return backlink ? { ...backlink, route: activeRoute } : null;
      }),
    ),
    { initialValue: null },
  );
}
