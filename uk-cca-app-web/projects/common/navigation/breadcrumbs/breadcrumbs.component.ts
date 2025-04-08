import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router, RouterLink } from '@angular/router';

import { filter } from 'rxjs';
import { BreadcrumbService, Link } from './breadcrumb.service';

@Component({
  selector: 'netz-breadcrumbs',
  standalone: true,
  template: `
    @if (showBreadcrumb()) {
      <div
        class="govuk-breadcrumbs govuk-breadcrumbs--collapse-on-mobile"
        [class.govuk-breadcrumbs--inverse]="inverse()"
      >
        <ol class="govuk-breadcrumbs__list">
          @for (breadcrumb of links(); track breadcrumb.link) {
            <li class="govuk-breadcrumbs__list-item">
              <a
                class="govuk-link"
                [routerLink]="breadcrumb.link"
                [queryParams]="breadcrumb.queryParams"
                [fragment]="breadcrumb.fragment"
                >{{ breadcrumb.text }}</a
              >
            </li>
          }
        </ol>
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
})
export class BreadcrumbsComponent {
  private readonly router = inject(Router);
  private readonly breadcrumbsService = inject(BreadcrumbService);

  links = this.breadcrumbsService.links;
  inverse = this.breadcrumbsService.inverse;
  showBreadcrumb = signal(true);

  constructor() {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      const breadcrumbData = this.extract();
      this.breadcrumbsService.links.set(breadcrumbData);
    });
  }

  /**
   *
   * @returns any breadcrumb data found in the current route tree, starting from the root route
   */
  private extract(): Link[] {
    const routeData: Link[] = [];
    let snapshot = this.router.routerState.snapshot.root;

    while (snapshot) {
      // feat: disable breadcrumb for the current route
      if (snapshot.data.breadcrumb === false && isLeaf(snapshot)) {
        return [];
      }

      if (!snapshot.data.breadcrumb) {
        snapshot = snapshot.firstChild;
        continue;
      }

      // if the current route is a leaf and has no url, we need to remove the last breadcrumb
      if (isLeaf(snapshot) && !snapshot.url.length) {
        return routeData.slice(0, -1);
      }

      // we need if a breadcrumb with the same text already exists
      // because the Angular router transfers the data route property from the parent route to the child route
      const text =
        typeof snapshot.data.breadcrumb === 'function'
          ? snapshot.data.breadcrumb(snapshot.data)
          : snapshot.data.breadcrumb;

      if (!hasText(routeData, text)) {
        routeData.push(createLink(text, snapshot.pathFromRoot));
      }

      snapshot = snapshot.firstChild;
    }

    return routeData;
  }
}

function createLink(text: string, pathFromRoot: ActivatedRouteSnapshot[]): Link {
  return {
    text,
    link: pathFromRoot.map((u) => u.url.toString()).join('/'),
  };
}

/**
 * @param links - the current breadcrumb links
 * @param t - the text to check
 * @returns boolean
 */
function hasText(links: Link[], t: unknown): boolean {
  if (typeof t !== 'string') return false;
  return links.map((l) => l.text).includes(t);
}

function isLeaf(snapshot: ActivatedRouteSnapshot): boolean {
  return !snapshot.firstChild;
}
