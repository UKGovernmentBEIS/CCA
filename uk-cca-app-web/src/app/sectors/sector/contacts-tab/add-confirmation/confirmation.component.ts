import { LowerCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { roleOptions } from '../../types';

@Component({
  selector: 'cca-add-sector-confirmation',
  standalone: true,
  template: `
    <div class="govuk-grid-row" data-testid="confirmation-screen">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>An account confirmation email has been sent to {{ email }} </govuk-panel>
        <h2 class="govuk-heading-m">What happens next</h2>
        <p class="govuk-body">
          The new {{ role | lowercase }} will be able to sign in to the service once they confirm their account.
        </p>
      </div>
    </div>
    <a class="govuk-link" routerLink="../../" fragment="contacts"> Return to: Contacts </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LowerCasePipe, PanelComponent, RouterLink],
})
export class AddSectorConfirmationComponent {
  route = inject(ActivatedRoute);
  email = this.route.snapshot.queryParamMap.get('email');
  roleCole = this.route.snapshot.queryParamMap.get('role');
  role = roleOptions.find((r) => r.value === this.roleCole)?.text;
}
