import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-error-page',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <netz-page-heading>{{ heading }}</netz-page-heading>
        <ng-content />
      </div>
    </div>
  `,
  standalone: true,
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorPageComponent {
  @Input() heading: string;
}
