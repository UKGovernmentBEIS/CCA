import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { AdminTerminationQuery } from '../../../+state/admin-termination.selectors';
import { toAdminTerminationReasonSummaryData } from '../../../admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-admin-termination-summary',
  template: `
    <div>
      <netz-page-heading caption="Admin termination">Summary</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForAdminTerminationSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  get downloadUrl() {
    const taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
    return `/tasks/${taskId}/file-download/`;
  }

  protected readonly summaryData = toAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(AdminTerminationQuery.selectAdminTerminationReasonDetails)(),
    this.requestTaskStore.select(AdminTerminationQuery.selectAdminTerminationSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );
}
