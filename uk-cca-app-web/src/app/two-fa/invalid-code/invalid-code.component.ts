import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-invalid-code',
  template: `
    <netz-page-heading>Invalid code</netz-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p>Invalid code. Please try again.</p>
      </div>
    </div>
  `,
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidCodeComponent {}
