import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { LinkDirective, PanelComponent } from '@netz/govuk-components';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';

@Component({
  selector: 'cca-add-operator-confirmation',
  template: ` <div class="govuk-grid-row">
    <div class="govuk-grid-row" data-testid="confirmation-screen">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>You have successfully added an operator user for {{ targetUnitName }}</govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard"> Go to my dashboard </a>
  </div>`,
  standalone: true,
  imports: [PanelComponent, LinkDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddOperatorConfirmationComponent {
  private readonly store = inject(ActiveTargetUnitStore);

  private readonly targetUnit = this.store.state.targetUnitAccountDetails;

  targetUnitName = `${this.targetUnit.businessId} - ${this.targetUnit.name}`;
}
