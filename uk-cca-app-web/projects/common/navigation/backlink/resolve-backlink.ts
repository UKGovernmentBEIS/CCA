import { ActivatedRouteSnapshot } from '@angular/router';

import { Backlink, RouteBacklink } from './backlink.interface';

/**
 * Resolves the `backlink` route data of a route to its object form.
 *
 * A function is called with the route data and snapshot. Returns `null` when the resolved back link is `false`,
 * `null`, `undefined` or empty, so the back link is hidden.
 */
export function resolveBacklink(route: ActivatedRouteSnapshot): Backlink | null {
  let backlink: RouteBacklink | null = route.data?.backlink;

  if (typeof backlink === 'function') backlink = backlink(route.data, route);
  if (!backlink) return null;

  return typeof backlink === 'string' ? { link: backlink } : backlink;
}
