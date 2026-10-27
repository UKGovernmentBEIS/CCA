import { Component, computed, input } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Params, RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-back-link',
  template: `
    <a [routerLink]="urlTree()" class="govuk-back-link" [class.govuk-back-link--inverse]="inverse()">Back</a>
  `,
  imports: [RouterLink],
})
export class BackLinkComponent {
  readonly link = input.required<string>();
  readonly route = input.required<ActivatedRouteSnapshot>();
  readonly fragment = input<string>();
  /** Query params of the link. Defaults to the current ones, or none when a fragment is set. */
  readonly queryParams = input<Params>();
  readonly inverse = input(false);

  // A fragment targets a section (e.g. a tab) of the destination page, which does not need the current query params
  protected readonly urlTree = computed(() =>
    createUrlTreeFromSnapshot(
      this.route(),
      [this.link()],
      this.queryParams() ?? (this.fragment() ? null : this.route().queryParams),
      this.fragment(),
    ),
  );
}
