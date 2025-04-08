import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';
import { SectorUserRoleCodePipe } from '@shared/pipes';

import { SectorUserInvitationStore } from '../sector-user-invitation/sector-user-invitation.store';

@Component({
  selector: 'cca-invitation-existing-confirmation',
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>
          You have been added as {{ storeUser.roleCode | sectorUserRoleCode }} to the account of
          {{ storeUser.sector }}
        </govuk-panel>

        <a class="govuk-link" [routerLink]="['/']" [replaceUrl]="true">Go to my dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PanelComponent, RouterLink, SectorUserRoleCodePipe],
})
export class InvitationExistingConfirmationComponent {
  private readonly store = inject(SectorUserInvitationStore);

  protected readonly storeUser = this.store.state;
}
