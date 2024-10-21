import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { FinalDecisionTypePipe } from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { AdminTerminationFinalDecisionQuery } from '../../../+state/admin-termination-final-decision.selectors';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../../admin-termination-final-decision.helper';
import { toFinalDecisionReasonSummaryData } from '../../../final-decision-reason-summary-data';

@Component({
  selector: 'cca-final-decision-reason-check-your-answers',
  templateUrl: './final-decision-reason-check-your-answers.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    FinalDecisionTypePipe,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly adminTerminationFinalDecisionTaskService = inject(TaskService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly finalDecisionType = this.requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )().finalDecisionType;

  protected readonly summaryData = toFinalDecisionReasonSummaryData(
    this.requestTaskStore.select(AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails)(),
    this.requestTaskStore.select(AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSaveFinalDecisionReason() {
    this.adminTerminationFinalDecisionTaskService
      .submitSubtask(ADMIN_TERMINATION_FINAL_DECISION_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
