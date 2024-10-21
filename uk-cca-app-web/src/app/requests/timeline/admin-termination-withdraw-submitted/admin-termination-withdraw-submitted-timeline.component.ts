import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components/summary';

import { AdminTerminationWithdrawSubmittedRequestActionPayload } from 'cca-api';

import { toAdminTerminationWithdrawSubmittedTimelineSummaryData } from './admin-termination-withdraw-submitted-summary-data';

@Component({
  selector: 'cca-admin-termination-withdraw-submited-timeline',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationWithdrawSubmittedTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as AdminTerminationWithdrawSubmittedRequestActionPayload;

  protected readonly data = toAdminTerminationWithdrawSubmittedTimelineSummaryData(
    this.actionPayload.adminTerminationWithdrawReasonDetails,
    this.actionPayload.adminTerminationWithdrawAttachments,
    this.actionPayload.decisionNotification,
    this.actionPayload.defaultContacts,
    this.actionPayload.officialNotice,
    `./file-download`,
    this.actionPayload.usersInfo,
  );
}
