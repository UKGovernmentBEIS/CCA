import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  TaskItemStatus,
  TasksApiService,
  toTrackActionSummaryData,
  TRACK_CORRECTIVE_ACTION_SUBTASK,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO, toSaveRequestTaskPayload } from '../../../transform';

@Component({
  selector: 'cca-track-corrective-actions-check-your-answers',
  template: `
    <div>
      <netz-page-heading [caption]="'Corrective action ' + actionId">Check your answers</netz-page-heading>
      <cca-summary [data]="data" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../../..">
      Return to: Track corrective actions {{ targetUnitAccountDetails()?.targetUnitAccountDetails?.businessId }}
    </a>
  `,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
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

  onSubmit() {
    const payload = this.requestTaskStore.select(trackCorrectiveActionsQuery.selectPayload)();
    const savePayload = toSaveRequestTaskPayload(payload, this.actionId);

    const currentSectionsCompleted = this.requestTaskStore.select(
      trackCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[`${TRACK_CORRECTIVE_ACTION_SUBTASK}${this.actionId}`] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, savePayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}
