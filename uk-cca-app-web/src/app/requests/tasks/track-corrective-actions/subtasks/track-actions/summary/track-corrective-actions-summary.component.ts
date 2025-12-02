import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toTrackActionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';

@Component({
  selector: 'cca-track-corrective-actions-summary',
  template: `
    <netz-page-heading [caption]="'Corrective action ' + actionId">Check your answers</netz-page-heading>
    <cca-summary [data]="data" />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../../..">
      Return to: Track corrective actions {{ targetUnitAccountDetails()?.targetUnitAccountDetails?.businessId }}
    </a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);

  protected readonly actionId = this.activatedRoute.snapshot.params.actionId;

  private readonly correctiveActionResponse = this.requestTaskStore.select(
    trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions,
  )()?.correctiveActionResponses[this.actionId];

  protected readonly targetUnitAccountDetails = toSignal(
    this.targetUnitAccountInfoViewService.getTargetUnitAccountDetailsById(
      this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.accountId,
    ),
  );

  protected readonly data = toTrackActionSummaryData(
    this.correctiveActionResponse,
    this.requestTaskStore.select(trackCorrectiveActionsQuery.selectFacilityAuditAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    '../../file-download',
  );
}
