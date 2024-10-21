import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { FinalDecisionTypePipe } from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { AdminTerminationFinalDecisionQuery } from '../../../+state/admin-termination-final-decision.selectors';
import { toFinalDecisionReasonSummaryData } from '../../../final-decision-reason-summary-data';

@Component({
  selector: 'cca-final-decision-reason-summary',
  templateUrl: './final-decision-reason-summary.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, FinalDecisionTypePipe, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

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
}
