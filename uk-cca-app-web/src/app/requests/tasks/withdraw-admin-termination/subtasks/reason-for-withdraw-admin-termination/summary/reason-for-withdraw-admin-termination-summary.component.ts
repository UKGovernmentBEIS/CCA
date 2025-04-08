import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { AdminTerminationWithdrawQuery } from '../../../+state/withdraw-admin-termination.selectors';
import { toWithdrawAdminTerminationReasonSummaryData } from '../../../withdraw-admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination-summary',
  templateUrl: './reason-for-withdraw-admin-termination-summary.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toWithdrawAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails)(),
    this.requestTaskStore.select(AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );
}
