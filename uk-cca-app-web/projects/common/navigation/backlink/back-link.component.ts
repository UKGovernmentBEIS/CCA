import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, Input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRouteSnapshot, Data, NavigationEnd, Router } from '@angular/router';

import { BehaviorSubject, filter } from 'rxjs';

import { BackLinkComponent as GovukBackLinkComponent } from '@netz/govuk-components';

import { RouteBacklink } from './backlink.interface';

@Component({
  selector: 'netz-back-link',
  template: `
    @if (backlink$ | async; as backlink) {
      <govuk-back-link [link]="backlink.link" [route]="backlink.route" [inverse]="inverse()" />
    }
  `,
  imports: [GovukBackLinkComponent, AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackLinkComponent {
  private readonly router = inject(Router);

  readonly inverse = input(false);

  protected readonly backlink$ = new BehaviorSubject<{ link: string; route: ActivatedRouteSnapshot }>(null);

  constructor() {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        let activeRoute = this.router.routerState.snapshot.root;
        while (activeRoute.firstChild) {
          activeRoute = activeRoute.firstChild;
        }

        if (this.hasBackLink(activeRoute.data)) {
          this.backlink$.next({ link: this.getLink(activeRoute), route: activeRoute });
        } else {
          this.backlink$.next(null);
        }
      });
  }

  private getLink(route: ActivatedRouteSnapshot): string {
    switch (typeof route.data.backlink) {
      case 'function':
        return route.data.backlink(route.data);
      case 'string':
      default:
        return route.data.backlink;
    }
  }

  private hasBackLink(routeData: Data): boolean {
    if (!routeData) return false;
    const backlink: RouteBacklink = routeData.backlink;
    if (typeof backlink === 'boolean' || typeof backlink === 'string') return !!backlink;
    if (typeof backlink === 'function') return !!backlink(routeData);
  }
}
