import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '../../shared/components/page-heading/page-heading.component';

@Component({
  selector: 'cca-invalid-code',
  template: `
    <cca-page-heading>Invalid code</cca-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p class="govuk-body">Invalid code. Please try again.</p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent],
})
export class InvalidCodeComponent {}
