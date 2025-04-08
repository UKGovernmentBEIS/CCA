import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { AdminTerminationFinalDecisionSubmittedRequestActionPayload } from 'cca-api';

import { toAdminTerminationFinalDecisionSubmittedTimelineSummaryData } from './admin-termination-final-decision-submitted-summary-data';

@Component({
  selector: 'cca-admin-termination-final-decision-submited-timeline',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationFinalDecisionSubmittedTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as AdminTerminationFinalDecisionSubmittedRequestActionPayload;

  protected readonly data = toAdminTerminationFinalDecisionSubmittedTimelineSummaryData(
    this.actionPayload.adminTerminationFinalDecisionReasonDetails,
    this.actionPayload.adminTerminationFinalDecisionAttachments,
    this.actionPayload.decisionNotification,
    this.actionPayload.defaultContacts,
    this.actionPayload.officialNotice,
    `./file-download`,
    this.actionPayload.usersInfo,
  );
}
