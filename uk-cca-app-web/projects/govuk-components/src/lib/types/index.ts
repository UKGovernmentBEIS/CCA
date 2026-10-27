import type { Params, QueryParamsHandling } from '@angular/router';

// -----------------------------------------------------------------------------
// Links — shared by every component that renders an anchor
// -----------------------------------------------------------------------------

/**
 * Describes where a GOV.UK link should navigate to.
 *
 * Use `routerLink` for navigation inside the app and `href` for anything that
 * leaves it. When both are supplied `routerLink` wins.
 */
export interface GovukLinkTarget {
  /** External URL. Rendered as a plain `href`. */
  href?: string;
  /** Internal route. Rendered with `RouterLink`, so no full page reload. */
  routerLink?: string | unknown[];
  queryParams?: Params | null;
  queryParamsHandling?: QueryParamsHandling | null;
  fragment?: string;
  preserveFragment?: boolean;
  skipLocationChange?: boolean;
  replaceUrl?: boolean;
  state?: Record<string, unknown>;
  /** `target` attribute, e.g. `_blank`. */
  target?: string;
  /** `rel` attribute. Set automatically to `noreferrer noopener` for `_blank`. */
  rel?: string;
}

/**
 * A link that can only navigate within the Angular application.
 */
export interface GovukRouterLinkTarget extends Omit<GovukLinkTarget, 'href' | 'routerLink' | 'target' | 'rel'> {
  /** Route this link navigates to. */
  routerLink: string | unknown[];
}

/**
 * A link rendered as list item text.
 */
export interface GovukLinkItem extends GovukLinkTarget {
  text: string;
  classes?: string;
  /** Extra context announced to assistive technology only. */
  visuallyHiddenText?: string;
}

// -----------------------------------------------------------------------------
// Footer
// -----------------------------------------------------------------------------

/** Grid column width for a footer navigation section. */
export type GovukFooterSectionWidth =
  'full' | 'one-half' | 'one-third' | 'two-thirds' | 'one-quarter' | 'three-quarters';

/** A column of links in the footer. */
export interface GovukFooterNavigation {
  title: string;
  items: GovukLinkItem[];
  /** Grid width, e.g. `two-thirds`. Defaults to `full`. */
  width?: GovukFooterSectionWidth;
  /** Splits the list over this many columns. */
  columns?: 1 | 2 | 3;
}

// -----------------------------------------------------------------------------
// Service navigation
// -----------------------------------------------------------------------------

/** An item in the service navigation. */
export interface GovukServiceNavigationItem extends GovukRouterLinkTarget {
  text: string;
}
