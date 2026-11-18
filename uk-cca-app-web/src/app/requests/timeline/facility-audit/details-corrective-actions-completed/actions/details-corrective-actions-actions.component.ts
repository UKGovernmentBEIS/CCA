import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { CorrectiveActionsSummaryDetailsComponent } from '@requests/common';

import { detailsCorrectiveActionsCompletedQuery } from '../details-corrective-actions-completed.selectors';

@Component({
  selector: 'cca-details-corrective-actions-actions',
  template: `
    <netz-page-heading caption="Corrective actions">Summary</netz-page-heading>
    <cca-corrective-actions-summary-details [correctiveActions]="correctiveActions()" [isEditable]="false" />
  `,
  imports: [PageHeadingComponent, CorrectiveActionsSummaryDetailsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsCorrectiveActionsActionsComponent {
  private readonly requestTaskActionStore = inject(RequestActionStore);

  private readonly selectAuditDetailsAndCorrectiveActions = this.requestTaskActionStore.select(
    detailsCorrectiveActionsCompletedQuery.selectAuditDetailsAndCorrectiveActions,
  );

  protected readonly correctiveActions = computed(
    () => this.selectAuditDetailsAndCorrectiveActions()?.correctiveActions,
  );
}
