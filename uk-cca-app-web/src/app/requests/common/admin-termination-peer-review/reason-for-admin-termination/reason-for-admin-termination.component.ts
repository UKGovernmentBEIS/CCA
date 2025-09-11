import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { adminTerminationPeerReviewQuery } from '../+state/admin-termination-peer-review-selectors';
import { toReasonForAdminTerminationDetailsSummaryData } from './to-reason-for-admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-admin-termination',
  template: `<cca-summary [data]="summaryData" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonForAdminTerminationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly payload = this.requestTaskStore.select(
    adminTerminationPeerReviewQuery.selectPeerReviewAdminTerminationReasonDetails,
  )();
  private readonly attachments = this.requestTaskStore.select(adminTerminationPeerReviewQuery.selectAttachments)();
  protected readonly summaryData = toReasonForAdminTerminationDetailsSummaryData(this.payload, this.attachments);
}
