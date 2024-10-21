import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-add-regulator-confirmation',
  standalone: true,
  imports: [PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: ` <div class="govuk-grid-row" data-testid="confirmation-screen">
    <div class="govuk-grid-column-two-thirds">
      <govuk-panel>An account confirmation email has been sent to {{ email }}</govuk-panel>
      <h2 class="govuk-heading-m">What happens next</h2>
      <p class="govuk-body">The new user will be able to log in once they confirm their account</p>
    </div>
  </div>`,
})
export class AddConfirmationComponent {
  route = inject(ActivatedRoute);
  email = this.route.snapshot.queryParamMap.get('email');
}
