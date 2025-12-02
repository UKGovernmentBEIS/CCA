import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TasksApiService, toAuditDetailsSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';

@Component({
  selector: 'cca-audit-details-summary',
  template: `
    <netz-page-heading caption="Audit details">Summary</netz-page-heading>
    <cca-summary [data]="data()" />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditDetailsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;

  private readonly auditDetailsAndCorrectiveActions = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions,
  );

  private readonly attachments = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectFacilityAuditAttachments,
  );

  protected readonly data = computed(() =>
    toAuditDetailsSummaryData(
      this.auditDetailsAndCorrectiveActions()?.auditDetails,
      this.attachments(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      generateDownloadUrl(this.taskId),
    ),
  );
}
