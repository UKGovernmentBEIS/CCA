import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { AdminTerminationSubmittedRequestActionPayload } from 'cca-api';

import { toAdminTerminationReasonSubmittedTimelineSummaryData } from './admin-termination-submitted-summary-data';

@Component({
  selector: 'cca-admin-termination-submited-timeline',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationSubmittedTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as AdminTerminationSubmittedRequestActionPayload;

  protected readonly data = toAdminTerminationReasonSubmittedTimelineSummaryData(
    this.actionPayload.adminTerminationReasonDetails,
    this.actionPayload.adminTerminationSubmitAttachments,
    this.actionPayload.decisionNotification,
    this.actionPayload.defaultContacts,
    this.actionPayload.officialNotice,
    `./file-download`,
    this.actionPayload.usersInfo,
  );
}
