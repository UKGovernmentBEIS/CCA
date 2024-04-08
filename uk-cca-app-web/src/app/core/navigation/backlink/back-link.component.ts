import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';

import { BehaviorSubject, filter, takeUntil } from 'rxjs';

import { getActiveRoute } from '@core/navigation/navigation.util';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { BackLinkComponent as GovukBackLinkComponent } from 'govuk-components';

@Component({
  selector: 'cca-back-link',
  template: ` @if (backlink$ | async; as backlink) {
    <govuk-back-link [link]="backlink.link" [route]="backlink.route"></govuk-back-link>
  }`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  standalone: true,
  imports: [AsyncPipe, GovukBackLinkComponent],
})
export class BackLinkComponent {
  protected backlink$ = new BehaviorSubject<{ link: string; route: ActivatedRouteSnapshot }>(null);

  constructor(
    private router: Router,
    private destroy$: DestroySubject,
  ) {
    this.router.events
      .pipe(
        takeUntil(this.destroy$),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        const activeRoute = getActiveRoute(router, true);

        if (activeRoute.data?.backlink) {
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
}
