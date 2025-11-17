import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';

@Component({
  selector: 'cca-add-operator-confirmation',
  template: ` <div class="govuk-grid-row">
    <div class="govuk-grid-row" data-testid="confirmation-screen">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>You have successfully added an operator user for {{ targetUnitName }}</govuk-panel>
      </div>
    </div>
    <a class="govuk-link" routerLink="/dashboard"> Go to my dashboard </a>
  </div>`,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddOperatorConfirmationComponent {
  private readonly store = inject(ActiveTargetUnitStore);

  private readonly targetUnit = this.store.state.targetUnitAccountDetails;

  protected readonly targetUnitName = `${this.targetUnit.businessId} - ${this.targetUnit.name}`;
}
