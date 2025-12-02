import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toAuditDetailsSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { detailsCorrectiveActionsCompletedQuery } from '../details-corrective-actions-completed.selectors';

@Component({
  selector: 'cca-details-corrective-actions-details',
  template: `
    <netz-page-heading caption="Audit details">Summary</netz-page-heading>
    <cca-summary [data]="data()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsCorrectiveActionsDetailsComponent {
  private readonly requestTaskActionStore = inject(RequestActionStore);

  private readonly selectAuditDetailsAndCorrectiveActions = this.requestTaskActionStore.select(
    detailsCorrectiveActionsCompletedQuery.selectAuditDetailsAndCorrectiveActions,
  );

  private readonly attachments = this.requestTaskActionStore.select(
    detailsCorrectiveActionsCompletedQuery.selectFacilityAuditAttachments,
  );

  protected readonly data = computed(() =>
    toAuditDetailsSummaryData(
      this.selectAuditDetailsAndCorrectiveActions()?.auditDetails,
      this.attachments(),
      false,
      '../../file-download',
    ),
  );
}
