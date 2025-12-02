import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toTrackActionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { trackCorrectiveActionsCompletedQuery } from '../track-corrective-actions-completed.selectors';

@Component({
  selector: 'cca-track-corrective-actions-completed-details',
  template: `
    <netz-page-heading [caption]="'Corrective action ' + actionId">Summary</netz-page-heading>
    <cca-summary [data]="data" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsCompletedDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly actionId = this.activatedRoute.snapshot.params.actionId;

  private readonly correctiveActionResponse = this.requestActionStore.select(
    trackCorrectiveActionsCompletedQuery.selectAuditTrackCorrectiveActions,
  )()?.correctiveActionResponses[this.actionId];

  protected readonly data = toTrackActionSummaryData(
    this.correctiveActionResponse,
    this.requestActionStore.select(trackCorrectiveActionsCompletedQuery.selectFacilityAuditAttachments)(),
    false,
    '../../file-download',
  );
}
