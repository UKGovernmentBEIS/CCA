import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { AdminTerminationWithdrawQuery } from '../../../+state/withdraw-admin-termination.selectors';
import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../../../withdraw-admin-termination.types';
import { toWithdrawAdminTerminationReasonSummaryData } from '../../../withdraw-admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Withdraw admin termination">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSaveReasonForAdminTerminationWithdraw()">
        Confirm and complete
      </button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly withdrawAdminTerminationTaskService = inject(TaskService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toWithdrawAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails)(),
    this.requestTaskStore.select(AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSaveReasonForAdminTerminationWithdraw() {
    this.withdrawAdminTerminationTaskService
      .submitSubtask(REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
