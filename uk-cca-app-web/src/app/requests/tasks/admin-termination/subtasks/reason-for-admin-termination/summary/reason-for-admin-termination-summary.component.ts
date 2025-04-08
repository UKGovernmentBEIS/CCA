import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { AdminTerminationQuery } from '../../../+state/admin-termination.selectors';
import { toAdminTerminationReasonSummaryData } from '../../../admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-admin-termination-summary',
  templateUrl: './reason-for-admin-termination-summary.component.html',
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
