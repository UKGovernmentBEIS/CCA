import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { auditDetailsCorrectiveActionsQuery } from '../../audit-details-corrective-actions.selectors';

@Component({
  selector: 'cca-pre-audit-review-confirmation',
  template: `
    <govuk-panel title="Audit details and corrective actions complete">
      Outcome <br />
      <span style="font-weight: bold;">{{ outcome() }}</span>
    </govuk-panel>

    @if (furtherActionsNeeded()) {
      <h3 class="govuk-heading-m">What happens next</h3>
      <p>
        A new task, 'Track corrective actions', has been assigned to you. You can review the corrective actions and
        upload any evidence that they have been completed when you start this task.
      </p>
    }

    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditDetailsAndCorrectiveActionsConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly auditDetailsAndCorrectiveActions = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions,
  );

  protected readonly furtherActionsNeeded = computed(
    () => this.auditDetailsAndCorrectiveActions()?.correctiveActions?.hasActions,
  );

  protected readonly outcome = computed(() =>
    this.furtherActionsNeeded() ? 'Corrective actions needed' : 'Corrective actions not needed',
  );
}
