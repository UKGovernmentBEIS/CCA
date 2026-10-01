import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRouteSnapshot, PRIMARY_OUTLET, RouterStateSnapshot, TitleStrategy } from '@angular/router';

import { environment } from 'src/environments/environment';

const GOV_UK_SUFFIX = 'GOV.UK';

/** Route data flag: the route title already contains the full service name, so only the GOV.UK suffix is appended. */
const TITLE_FULL_SERVICE_NAME = 'titleFullServiceName';

/**
 * Sets the document title from the route config.
 *
 * Format: `${title} - ${serviceNameShort} - GOV.UK`, where `title` is the leaf
 * route's `title` composed with the nearest titled ancestor
 * ("Summary - Review target unit details"). Routes flagged with
 * `data: { titleFullServiceName: true }` (the landing page) append only
 * " - GOV.UK". Routes without any title fall back to "CCA - GOV.UK".
 */
@Injectable({ providedIn: 'root' })
export class CcaTitleStrategy extends TitleStrategy {
  private readonly titleService = inject(Title);

  override updateTitle(snapshot: RouterStateSnapshot): void {
    const { title, useFullServiceName } = this.resolveTitle(snapshot.root);

    if (!title) {
      this.titleService.setTitle(`${environment.serviceNameShort} - ${GOV_UK_SUFFIX}`);
      return;
    }

    const suffix = useFullServiceName ? GOV_UK_SUFFIX : `${environment.serviceNameShort} - ${GOV_UK_SUFFIX}`;
    this.titleService.setTitle(`${title} - ${suffix}`);
  }

  private resolveTitle(route: ActivatedRouteSnapshot): { title: string | undefined; useFullServiceName: boolean } {
    const titles: string[] = [];
    let leafRoute: ActivatedRouteSnapshot | undefined;

    let current: ActivatedRouteSnapshot | undefined = route;
    while (current) {
      const own = this.resolveRouteTitle(current);
      if (own) {
        titles.push(own);
      }
      leafRoute = current;
      current = current.children.find((child) => child.outlet === PRIMARY_OUTLET);
    }

    const leaf = titles.at(-1);
    const nearestTitledAncestor = titles.length > 1 ? titles[titles.length - 2] : undefined;
    const title =
      leaf && nearestTitledAncestor && leaf !== nearestTitledAncestor ? `${leaf} - ${nearestTitledAncestor}` : leaf;

    return { title, useFullServiceName: leafRoute?.data[TITLE_FULL_SERVICE_NAME] === true };
  }

  private resolveRouteTitle(route: ActivatedRouteSnapshot): string | undefined {
    const configTitle = route.routeConfig?.title;
    if (typeof configTitle === 'string') {
      return configTitle;
    }
    // Resolver titles are resolved into the snapshot during navigation.
    return configTitle ? route.title : undefined;
  }
}
