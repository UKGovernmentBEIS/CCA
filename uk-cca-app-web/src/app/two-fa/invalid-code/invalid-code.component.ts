import { ChangeDetectionStrategy, Component } from '@angular/core';

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
})
export class InvalidCodeComponent {}
