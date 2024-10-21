import { JsonPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective, LinkDirective, PanelComponent } from '@netz/govuk-components';

import { OperatorUserInvitationStore } from '../store';

@Component({
  selector: 'cca-invitation-existing-confirmation',
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel> You have been added as an operator user for {{ storeUser.accountName }} </govuk-panel>

        <a govukLink [routerLink]="['/dashboard']" [replaceUrl]="true">Go to my dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PanelComponent, ButtonDirective, LinkDirective, RouterLink, JsonPipe],
})
export class InvitationExistingConfirmationComponent {
  private readonly store = inject(OperatorUserInvitationStore);

  protected readonly storeUser = this.store.state;
}
