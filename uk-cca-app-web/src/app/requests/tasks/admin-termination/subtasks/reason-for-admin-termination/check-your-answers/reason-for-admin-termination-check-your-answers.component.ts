import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { SummaryComponent } from '@shared/components/summary';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { AdminTerminationQuery } from '../../../+state/admin-termination.selectors';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../../../admin-termination.types';
import { toAdminTerminationReasonSummaryData } from '../../../admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-admin-termination-check-your-answers',
  templateUrl: './reason-for-admin-termination-check-your-answers.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForAdminTerminationSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly adminTerminationTaskService = inject(TaskService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(AdminTerminationQuery.selectAdminTerminationReasonDetails)(),
    this.requestTaskStore.select(AdminTerminationQuery.selectAdminTerminationSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSaveReasonForAdminTermination() {
    this.adminTerminationTaskService
      .submitSubtask(REASON_FOR_ADMIN_TERMINATION_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
