import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-breadcrumbs',
  standalone: true,
  template: `
    <div class="govuk-breadcrumbs govuk-breadcrumbs--collapse-on-mobile" [class.govuk-breadcrumbs--inverse]="inverse">
      <ol class="govuk-breadcrumbs__list">
        <ng-content></ng-content>
      </ol>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbsComponent {
  @Input() inverse = false;
}
