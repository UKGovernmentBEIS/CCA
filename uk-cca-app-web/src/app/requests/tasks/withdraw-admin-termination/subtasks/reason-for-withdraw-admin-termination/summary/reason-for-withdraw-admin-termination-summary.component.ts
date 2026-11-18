import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { adminTerminationWithdrawQuery } from '../../../withdraw-admin-termination.selectors';
import { toWithdrawAdminTerminationReasonSummaryData } from '../../../withdraw-admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination-summary',
  template: `
    <div>
      <netz-page-heading caption="Withdraw admin termination">Summary</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toWithdrawAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(adminTerminationWithdrawQuery.selectReasonDetails)(),
    this.requestTaskStore.select(adminTerminationWithdrawQuery.selectAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );
}
