import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { LinkDirective, PanelComponent } from '@netz/govuk-components';

import { AdminTerminationFinalDecisionQuery } from '../../+state/admin-termination-final-decision.selectors';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds govuk-body">
        <govuk-panel>Admin termination final decision notice sent to operator</govuk-panel>

        <p>
          The admin termination agreement has been
          {{ finalDecisionType === 'TERMINATE_AGREEMENT' ? 'terminated' : 'withdrawn' }}.
        </p>

        <p>The selected users will receive an email notification of your decision.</p>

        <a govukLink routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, LinkDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly finalDecisionType = this.requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )().finalDecisionType;
}
