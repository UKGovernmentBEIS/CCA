import { ActivatedRouteSnapshot, Data, Params } from '@angular/router';

/** Object form of backlink route data. */
export type Backlink = {
  /** Url relative to the current route. */
  link: string;
  /** Fragment appended to the resolved link, e.g. the id of the tab the user came from. */
  fragment?: string;
  /**
   * Query params of the resolved link. Defaults to the current route's query params, or none when `fragment` is set.
   * Pass `{}` to drop the current ones, e.g. when they only mark how the current page was entered.
   */
  queryParams?: Params;
};

/**
 * Value accepted by the `backlink` route data property.
 *
 * A string is a url relative to the current route; `false` hides the back link; a function builds the back link
 * from the route data and snapshot (e.g. to pick a link by the query params the route was entered with) and hides
 * it when it returns `false` or `null`.
 */
export type RouteBacklink =
  string | false | Backlink | ((data: Data, route: ActivatedRouteSnapshot) => string | false | null | Backlink);
