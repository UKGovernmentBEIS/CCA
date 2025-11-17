import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-add-regulator-confirmation',
  template: ` <div class="govuk-grid-row" data-testid="confirmation-screen">
    <div class="govuk-grid-column-two-thirds">
      <govuk-panel>An account confirmation email has been sent to {{ email }}</govuk-panel>
      <h2 class="govuk-heading-m">What happens next</h2>
      <p>The new user will be able to log in once they confirm their account</p>
    </div>
  </div>`,
  imports: [PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddConfirmationComponent {
  private readonly route = inject(ActivatedRoute);

  protected readonly email = this.route.snapshot.queryParamMap.get('email');
}
