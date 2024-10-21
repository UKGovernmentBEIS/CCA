import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { AuthService } from '@core/services/auth.service';
import { ButtonDirective, PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-invitation-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>You've successfully created a user account</govuk-panel>

        <p class="govuk-body">
          When you sign in to the CCA reporting service for the first time, you'll be asked to set up two-factor
          authentication.
        </p>

        <h3 class="govuk-heading-m">What happens next</h3>

        <p class="govuk-body">You can sign in to the CCA reporting service.</p>
        <button ccaPendingButton govukButton type="button" (click)="onSignIn()">Sign in</button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PanelComponent, ButtonDirective],
})
export class InvitationConfirmationComponent {
  private readonly authService = inject(AuthService);
  onSignIn() {
    this.authService.login();
  }
}
