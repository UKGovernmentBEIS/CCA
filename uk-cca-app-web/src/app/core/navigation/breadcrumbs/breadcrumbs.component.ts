import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, WritableSignal } from '@angular/core';
import { ActivatedRouteSnapshot, Data, NavigationEnd, Route, Router, RouterModule } from '@angular/router';

import { filter, takeUntil } from 'rxjs';

import { getActiveRoute } from '@core/navigation/navigation.util';

import { BreadcrumbsComponent as GovukBreadcrumbsComponent, LinkDirective } from 'govuk-components';

import { DestroySubject } from '../../services/destroy-subject.service';
import { BREADCRUMB_ITEMS } from './breadcrumbs.factory';
import { BreadcrumbItem } from './breadcrumbs.interface';

@Component({
  selector: 'cca-breadcrumbs',
  template: ` @if (breadcrumbs(); as breadcrumbs) {
    <govuk-breadcrumbs>
      @for (breadcrumb of breadcrumbs; track breadcrumb.text; let i = $index) {
        @if (breadcrumb.link) {
          <a govukLink="breadcrumb" [routerLink]="breadcrumb.link" [queryParams]="breadcrumb.queryParams">{{
            breadcrumb.text
          }}</a>
        } @else {
          <li class="govuk-breadcrumbs__list-item" govukLink="breadcrumb">{{ breadcrumb.text }}</li>
        }
      }
    </govuk-breadcrumbs>
  }`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  standalone: true,
  imports: [AsyncPipe, RouterModule, LinkDirective, GovukBreadcrumbsComponent],
})
export class BreadcrumbsComponent {
  constructor(
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    @Inject(BREADCRUMB_ITEMS) protected breadcrumbs: WritableSignal<BreadcrumbItem[]>,
  ) {
    this.router.events
      .pipe(
        takeUntil(this.destroy$),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        const root = router.routerState.snapshot.root;
        const activeRoute = getActiveRoute(router, true);

        if (activeRoute.routeConfig.data?.breadCrumb !== false) {
          const breadcrumbs: BreadcrumbItem[] = [];
          this.addBreadcrumb(root, [], breadcrumbs);
          this.breadcrumbs.set(breadcrumbs);
        } else {
          this.breadcrumbs.set([]);
        }
      });
  }

  private addBreadcrumb(route: ActivatedRouteSnapshot, parentUrl: string[], breadcrumbs: BreadcrumbItem[]): void {
    if (route) {
      const routeUrl = parentUrl.concat(route.url.map((url) => url.path));

      if (route.data.breadcrumb) {
        const breadcrumb: BreadcrumbItem = {
          text: this.getBreadcrumbText(route.data, route.title),
          link: this.getBreadcrumbLink(route, routeUrl),
          queryParams: route.queryParams ?? {},
        };

        if (!this.alreadyHasBreadcrumb(breadcrumbs, breadcrumb)) {
          breadcrumbs.push(breadcrumb);
        }
      }

      this.addBreadcrumb(route.firstChild, routeUrl, breadcrumbs);
    }
  }

  private getBreadcrumbText(data: Data, title?: string): string {
    const breadcrumb = data.breadcrumb;

    return this.hasTextResolutionFunction(breadcrumb)
      ? breadcrumb.resolveText(data)
      : typeof breadcrumb === 'function'
        ? breadcrumb(data)
        : typeof breadcrumb === 'boolean'
          ? data.pageTitle ?? title
          : breadcrumb;
  }

  private getBreadcrumbLink(route: ActivatedRouteSnapshot, routeUrl: string[]): string[] {
    let currentRoute = route;
    let currentRouteUrl = routeUrl;

    if (
      (!this.hasComponent(route) || this.mustSkipLink(currentRoute.data)) &&
      this.mustSkipLink(currentRoute.data) !== false
    ) {
      while (currentRoute.firstChild) {
        currentRoute = currentRoute.firstChild;

        if (currentRoute.routeConfig.data?.breadcrumb) {
          break;
        }

        if (this.hasComponent(currentRoute)) {
          currentRouteUrl = currentRouteUrl.concat(currentRoute.url.map((url) => url.path));
          break;
        }

        currentRouteUrl = currentRouteUrl.concat(currentRoute.url.map((url) => url.path));
      }
    }

    return this.hasMoreUrlSegments(route) ? ['/' + currentRouteUrl.join('/')] : null;
  }

  private alreadyHasBreadcrumb(breadcrumbs: BreadcrumbItem[], breadcrumb: BreadcrumbItem): boolean {
    return breadcrumbs.map((b) => b.text).includes(breadcrumb.text);
  }

  private hasMoreUrlSegments(route: ActivatedRouteSnapshot): boolean {
    let remainingUrl = '';
    let currentRoute = route;
    while (currentRoute.firstChild) {
      currentRoute = currentRoute.firstChild;
      remainingUrl = [remainingUrl, currentRoute.url.join('')].join('');
    }

    return remainingUrl.length > 0;
  }

  private hasComponent(route: ActivatedRouteSnapshot): boolean {
    const children = route.routeConfig.children;
    const hasChildren = children?.length > 0;
    const childWithEmptyPath: Route = hasChildren && children.find((child) => child.path.length === 0);

    return (!!route.routeConfig.component && !hasChildren) || !!childWithEmptyPath?.component;
  }

  private hasTextResolutionFunction(breadcrumb: any): boolean {
    return (
      typeof breadcrumb === 'object' && 'resolveText' in breadcrumb && typeof breadcrumb.resolveText === 'function'
    );
  }

  private mustSkipLink(data: Data): boolean {
    return (
      typeof data.breadcrumb === 'object' &&
      'skipLink' in data.breadcrumb &&
      ((typeof data.breadcrumb.skipLink === 'boolean' && data.breadcrumb.skipLink === true) ||
        (typeof data.breadcrumb.skipLink === 'function' && data.breadcrumb.skipLink(data) === true))
    );
  }
}
