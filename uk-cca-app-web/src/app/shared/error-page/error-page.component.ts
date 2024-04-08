import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

@Component({
  selector: 'cca-error-page',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <cca-page-heading>{{ heading }}</cca-page-heading>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent]
})
export class ErrorPageComponent {
  @Input() heading: string;
}
