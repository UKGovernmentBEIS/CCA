import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, Input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRouteSnapshot, Data, NavigationEnd, Router } from '@angular/router';

import { BehaviorSubject, filter } from 'rxjs';

import { BackLinkComponent as GovukBackLinkComponent } from '@netz/govuk-components';

import { getActiveRoute } from '../navigation.util';
import { RouteBacklink } from './backlink.interface';

@Component({
  selector: 'netz-back-link',
  standalone: true,
  template: ` <govuk-back-link
    *ngIf="backlink$ | async as backlink"
    [link]="backlink.link"
    [route]="backlink.route"
    [inverse]="inverse"
  ></govuk-back-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [GovukBackLinkComponent, NgIf, AsyncPipe],
})
export class BackLinkComponent {
  @Input() inverse = false;
  protected backlink$ = new BehaviorSubject<{ link: string; route: ActivatedRouteSnapshot }>(null);

  constructor(
    readonly router: Router,
    private readonly destroy$: DestroyRef,
  ) {
    router.events
      .pipe(
        takeUntilDestroyed(this.destroy$),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        const activeRoute = getActiveRoute(router, true);

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
