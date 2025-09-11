import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ButtonDirective, PanelComponent } from '@netz/govuk-components';
import { AuthService } from '@shared/services';

@Component({
  selector: 'cca-invitation-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>You've successfully created a user account</govuk-panel>

        <p>
          When you sign in to the CCA reporting service for the first time, you'll be asked to set up two-factor
          authentication.
        </p>

        <h3 class="govuk-heading-m">What happens next</h3>

        <p>You can sign in to the CCA reporting service.</p>
        <button netzPendingButton govukButton type="button" (click)="onSignIn()">Sign in</button>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvitationConfirmationComponent {
  private readonly authService = inject(AuthService);

  onSignIn() {
    this.authService.login();
  }
}
