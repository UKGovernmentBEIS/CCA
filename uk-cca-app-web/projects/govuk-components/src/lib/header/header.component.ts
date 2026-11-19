import { NgTemplateOutlet } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { isRouterLink } from '../core/link';
import { type GovukLinkTarget } from '../types';

/**
 * GOV.UK header.
 *
 * The blue brand-coloured bar carrying the GOV.UK logo. Service names and
 * navigation belong in the service navigation component below it, not here.
 * Projected content is rendered in the actions area on the right-hand side of
 * the bar.
 *
 * Wrap the component in `<header class="govuk-template__header">`, as in the
 * GOV.UK Frontend v6 page template, to keep the banner landmark on the page.
 *
 * ```html
 * <header class="govuk-template__header">
 *   <govuk-header>
 *     <a class="govuk-link govuk-link--inverse" routerLink (click)="logout($event)">Sign out</a>
 *   </govuk-header>
 * </header>
 * ```
 */
@Component({
  selector: 'govuk-header',
  templateUrl: './header.component.html',
  host: { class: 'govuk-header' },
  styleUrl: './header.component.scss',
  imports: [RouterLink, NgTemplateOutlet],
})
export class GovukHeaderComponent {
  /** Where the GOV.UK logo links to. Defaults to `//gov.uk`. */
  readonly homepage = input<GovukLinkTarget>();

  /** Name of the product, shown next to the logo. */
  readonly productName = input<string>();

  /** Classes for the inner container, e.g. `govuk-width-container--wide`. */
  readonly containerClasses = input('govuk-width-container');

  protected readonly containerClass = computed(() => `govuk-header__container ${this.containerClasses()}`.trim());

  protected readonly isRouterLink = isRouterLink;
}
