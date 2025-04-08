import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-invalid-code',
  template: `
    <netz-page-heading>Invalid code</netz-page-heading>
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
