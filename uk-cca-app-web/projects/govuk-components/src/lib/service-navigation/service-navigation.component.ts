import { isPlatformBrowser } from '@angular/common';
import { booleanAttribute, Component, computed, inject, input, PLATFORM_ID, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { IsActiveMatchOptions, RouterLink, RouterLinkActive } from '@angular/router';

import { debounceTime, distinctUntilChanged, EMPTY, fromEvent, map } from 'rxjs';

import { isRouterLink } from '../core/link';
import type { GovukLinkTarget, GovukServiceNavigationItem } from '../types';

/**
 * GOV.UK service navigation.
 *
 * Shows the service name and its top-level navigation. Below the desktop
 * breakpoint the links collapse behind a Menu button.
 *
 * Items with a `routerLink` pick up their active state from the router, so
 * `aria-current` stays correct as the user navigates without any extra wiring.
 * An item is active on its path and every path below it, whatever the query
 * params or fragment: an item's query params only set where the link lands.
 *
 * ```html
 * <govuk-service-navigation
 *   serviceName="Manage your Climate Change Agreements"
 *   [serviceHomepage]="{ routerLink: '/' }"
 *   [navigation]="[{ text: 'Tasks', routerLink: '/dashboard' }]"
 * />
 * ```
 */
@Component({
  selector: 'govuk-service-navigation',
  templateUrl: './service-navigation.component.html',
  host: {
    class: 'govuk-service-navigation',
    '[attr.role]': 'serviceName() ? "region" : null',
    '[attr.aria-label]': 'serviceName() ? ariaLabel() : null',
  },
  styles: ':host { display: block; }',
  imports: [RouterLink, RouterLinkActive],
})
export class GovukServiceNavigationComponent {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  /** The name of your service. */
  readonly serviceName = input<string>();

  /** The homepage of your service. */
  readonly serviceHomepage = input<GovukLinkTarget>();

  /** The navigation items shown in the service header. */
  readonly navigation = input<GovukServiceNavigationItem[]>([]);

  /**
   * The text for the `aria-label` which labels the service navigation
   * container when a service name is included. Defaults to `Service information`.
   */
  readonly ariaLabel = input('Service information');

  /**
   * The screen reader label for the mobile navigation menu. Defaults to the
   * same value as `menuButtonText` if not specified.
   */
  readonly navigationLabel = input<string>();

  /** The text of the mobile navigation menu toggle. Defaults to `Menu`. */
  readonly menuButtonText = input('Menu');

  /**
   * The screen reader label for the mobile navigation menu toggle. Defaults
   * to the same value as `menuButtonText` if not specified.
   */
  readonly menuButtonLabel = input<string>();

  /**
   * The ID used to associate the mobile navigation toggle with the navigation
   * menu. Defaults to `navigation`.
   */
  readonly navigationId = input('navigation');

  /**
   * Collapses the links behind a Menu button on small screens.
   *
   * Left unset it follows GDS and collapses only when there is more than one
   * link — a lone link is shorter than the button that would hide it.
   */
  readonly collapseOnMobile = input<boolean | undefined, unknown>(undefined, {
    transform: (value) => (value === undefined ? undefined : booleanAttribute(value)),
  });

  private readonly breakpoint = '(min-width: 641px)';

  protected readonly menuOpen = signal(false);

  /**
   * Query params are ignored so an item stays active when the page changes
   * them, e.g. through filters or tabs.
   */
  protected readonly activeOptions: IsActiveMatchOptions = {
    paths: 'subset',
    queryParams: 'ignored',
    matrixParams: 'ignored',
    fragment: 'ignored',
  };

  /**
   * On the server there is no viewport to measure. Assuming desktop produces
   * the same markup GDS ships without JavaScript — links visible, Menu button
   * hidden — so the page is usable before hydration and the first client-side
   * render corrects it for small screens.
   */
  readonly isDesktop = toSignal(
    this.isBrowser
      ? fromEvent(window, 'resize').pipe(
          map(() => window.matchMedia(this.breakpoint).matches),
          debounceTime(20),
          distinctUntilChanged(),
        )
      : EMPTY,
    { initialValue: this.isBrowser ? window.matchMedia(this.breakpoint).matches : true },
  );

  protected readonly collapsible = computed(() => this.collapseOnMobile() ?? this.navigation().length > 1);

  /** The list is only hidden on small screens with the menu closed. */
  protected readonly menuHidden = computed(() => this.collapsible() && !this.isDesktop() && !this.menuOpen());

  /**
   * GDS drops the `aria-label` when it repeats the button text, so screen
   * readers announce the name once.
   */
  protected readonly menuButtonAriaLabel = computed(() => {
    const label = this.menuButtonLabel();
    return label && label !== this.menuButtonText() ? label : null;
  });

  /**
   * Only the service homepage forks on this: it is a plain `GovukLinkTarget`
   * and may leave the app. Navigation items always route.
   */
  protected readonly isRouterLink = isRouterLink;

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }
}
