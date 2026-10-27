import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRouteSnapshot, NavigationEnd, Router, RouterLink } from '@angular/router';

import { filter } from 'rxjs';

import { BreadcrumbService, Link } from './breadcrumb.service';
import { createLink } from './breadcrumb-link';

@Component({
  selector: 'netz-breadcrumbs',
  template: `
    @if (showBreadcrumb()) {
      <nav
        class="govuk-breadcrumbs govuk-breadcrumbs--collapse-on-mobile"
        aria-label="Breadcrumb"
        [class.govuk-breadcrumbs--inverse]="inverse()"
      >
        <ol class="govuk-breadcrumbs__list">
          @for (breadcrumb of links(); track breadcrumb.link) {
            <li class="govuk-breadcrumbs__list-item">
              <a
                class="govuk-breadcrumbs__link"
                [routerLink]="breadcrumb.link"
                [queryParams]="breadcrumb.queryParams"
                [fragment]="breadcrumb.fragment"
                >{{ breadcrumb.text }}</a
              >
            </li>
          }
        </ol>
      </nav>
    }
  `,
  imports: [RouterLink],
})
export class BreadcrumbsComponent {
  private readonly router = inject(Router);
  private readonly breadcrumbsService = inject(BreadcrumbService);

  readonly links = this.breadcrumbsService.links;
  readonly inverse = this.breadcrumbsService.inverse;
  readonly showBreadcrumb = signal(true);

  constructor() {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        let breadcrumbData = this.extract();
        const snapshot = this.router.routerState.snapshot;
        const path = snapshot.url.toString().split('#')[0].split('?')[0];
        breadcrumbData = removeSameRouteBreadcrumb(breadcrumbData, path);
        this.breadcrumbsService.links.set(breadcrumbData);
      });
  }

  /**
   * @returns any breadcrumb data found in the current route tree, starting from the root route
   */
  private extract(): Link[] {
    const routeData: Link[] = [];
    let snapshot: ActivatedRouteSnapshot | null = this.router.routerState.snapshot.root;

    while (snapshot) {
      // disable breadcrumb for the current route
      if (snapshot.data.breadcrumb === false && isLeaf(snapshot)) return [];

      if (!snapshot.data.breadcrumb) {
        snapshot = snapshot.firstChild;
        continue;
      }

      const breadcrumb = createLink(snapshot);

      if (breadcrumb === null) {
        snapshot = snapshot.firstChild;
        continue;
      }

      // Angular router transfers the data route property from the parent route to the child route.
      // That's why we need to determine if a breadcrumb with the same text already exists
      if (!hasText(routeData, breadcrumb.text)) {
        routeData.push(breadcrumb);
      } else {
        const idx = routeData.findIndex((r) => r.text === breadcrumb.text);
        routeData[idx].fragment = breadcrumb.fragment;
      }

      snapshot = snapshot.firstChild;
    }

    return routeData;
  }
}

/**
 * Removes the same breadcrumb from the breadcrumb data array
 * @param breadcrumbData
 * @param path
 * @returns
 */
function removeSameRouteBreadcrumb(breadcrumbData: Link[], path: string): Link[] {
  const bd = structuredClone(breadcrumbData);
  const index = bd.findIndex((b) => b.link === path);

  if (index >= 0) bd.splice(index);

  // A lone home crumb adds no value: the top-level sections are already in the
  // service navigation, so the breadcrumb is hidden on those pages. Deeper
  // routes keep the full trail (e.g. Dashboard > Manage sectors).
  const onlyHomeCrumb = bd.length === 1 && bd[0].link === '/';
  return onlyHomeCrumb ? [] : bd;
}

/**
 * @param links - the current breadcrumb links
 * @param t - the text to check
 * @returns boolean
 */
function hasText(links: Link[], t: unknown): boolean {
  if (typeof t !== 'string') return false;
  return links.some((l) => l.text === t);
}

/**
 * Checks if the current route is a leaf node
 * @param snapshot
 * @returns
 */
function isLeaf(snapshot: ActivatedRouteSnapshot): boolean {
  return !snapshot.firstChild;
}
