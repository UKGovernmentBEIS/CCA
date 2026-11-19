import { booleanAttribute, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { isRouterLink, resolveRel } from '../core/link';
import type { GovukFooterNavigation, GovukLinkItem } from '../types';

/**
 * GOV.UK footer.
 *
 * Wrap the component in `<footer class="govuk-template__footer">`, as in the
 * GOV.UK Frontend v6 page template, to keep the contentinfo landmark on the page.
 *
 * ```html
 * <footer class="govuk-template__footer">
 *   <govuk-footer
 *     [meta]="[
 *       { text: 'Cookies', routerLink: '/cookies' },
 *       { text: 'Accessibility Statement', routerLink: '/accessibility' },
 *     ]" />
 * </footer>
 * ```
 */
@Component({
  selector: 'govuk-footer',
  templateUrl: './footer.component.html',
  host: { class: 'govuk-footer' },
  styles: ':host { display: block; } .govuk-footer__meta-custom:empty { display: none; }',
  imports: [RouterLink],
})
export class GovukFooterComponent {
  /** Columns of links shown above the meta row. */
  readonly navigation = input<GovukFooterNavigation[]>([]);

  /** Support links shown in the meta row. */
  readonly meta = input<GovukLinkItem[]>([]);

  readonly metaVisuallyHiddenTitle = input('Support links');

  /** Set to false for content not covered by the Open Government Licence. */
  readonly showContentLicence = input(true, { transform: booleanAttribute });

  readonly copyright = input('© Crown copyright');

  readonly containerClasses = input('');

  protected readonly containerClass = computed(() => `govuk-width-container ${this.containerClasses()}`.trim());

  protected readonly isRouterLink = isRouterLink;
  protected readonly rel = resolveRel;

  protected listClasses(section: GovukFooterNavigation): string {
    return section.columns ? `govuk-footer__list govuk-footer__list--columns-${section.columns}` : 'govuk-footer__list';
  }
}
