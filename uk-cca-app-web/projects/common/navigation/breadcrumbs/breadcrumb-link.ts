import { ActivatedRouteSnapshot, Data } from '@angular/router';

import { Link } from './breadcrumb.service';

/** Object form of breadcrumb route data. */
export type Breadcrumb = {
  /** Text shown in the breadcrumb. */
  text: string;
  /** Explicit link target. Takes precedence over `from`. */
  link?: string | string[];
  /**
   * Route config `path` of an ancestor to link to, e.g. `':facilityId'`.
   *
   * The current route is never matched. The innermost matching ancestor wins, so avoid repeating a
   * `path` in a route chain when you need to link to the outer route: only the innermost one can be
   * targeted.
   */
  from?: string;
  /** Fragment appended to the resolved link. */
  fragment?: string;
};

/**
 * Value accepted by the `breadcrumb` route data property.
 *
 * A string shows the text and links to the current route; `false` hides the breadcrumb on the
 * current route; a function builds the breadcrumb from resolved route data and hides the breadcrumb
 * when it returns `false` or `null`.
 *
 * Pass the resolved data type, e.g. `BreadcrumbData<{ facilityDetails: FacilityInfoDTO }>`, to
 * type the function argument.
 */
export type BreadcrumbData<TData = Data> =
  string | false | Breadcrumb | ((data: TData) => string | false | null | Breadcrumb);

/**
 * Builds the link for a route's breadcrumb.
 *
 * `link` takes precedence. When `link` is not set and `from` is, the link resolves to the URL of the nearest
 * ancestor route whose config `path` matches `from` (e.g. `:facilityId`). The current route itself is never
 * matched. This lets a page point its crumb at a parent route regardless of the URL tree it was entered
 * through. Falls back to the snapshot's own URL when the ancestor is not found.
 *
 * Returns `null` when the resolved breadcrumb is `false`, `null` or `undefined`, so a breadcrumb
 * function can hide the crumb.
 */
export function createLink(snapshot: ActivatedRouteSnapshot): Link | null {
  let breadcrumbRouteData = snapshot.data.breadcrumb;
  let text = '';
  let fragment: string | undefined;
  let link: string | string[] = snapshotUrl(snapshot);

  if (typeof breadcrumbRouteData === 'function') breadcrumbRouteData = breadcrumbRouteData(snapshot.data);

  if (breadcrumbRouteData === false || breadcrumbRouteData === null || breadcrumbRouteData === undefined) {
    return null;
  }

  if (typeof breadcrumbRouteData === 'string') {
    text = breadcrumbRouteData;
  } else if (breadcrumbRouteData && typeof breadcrumbRouteData === 'object' && 'text' in breadcrumbRouteData) {
    text = breadcrumbRouteData.text;
    fragment = breadcrumbRouteData.fragment;

    if (breadcrumbRouteData.link) {
      link = breadcrumbRouteData.link;
    } else if (breadcrumbRouteData.from !== undefined) {
      const ancestor = findAncestor(snapshot, breadcrumbRouteData.from);
      if (ancestor) link = snapshotUrl(ancestor);
    }
  }

  return { text, link, fragment };
}

function snapshotUrl(snapshot: ActivatedRouteSnapshot): string {
  const path = snapshot.pathFromRoot
    .map((route) => route.url.join('/'))
    .filter(Boolean)
    .join('/');

  return `/${path}`;
}

function findAncestor(snapshot: ActivatedRouteSnapshot, path: string): ActivatedRouteSnapshot | undefined {
  const pathFromRoot = snapshot.pathFromRoot;

  // Start at the parent: `from` links to an ancestor, never to the route itself.
  for (let index = pathFromRoot.length - 2; index >= 0; index--) {
    if (pathFromRoot[index].routeConfig?.path === path) return pathFromRoot[index];
  }

  return undefined;
}
