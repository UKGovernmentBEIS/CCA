import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { FinalDecisionTypePipe } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';

import { AdminTerminationFinalDecisionQuery } from '../../../+state/admin-termination-final-decision.selectors';
import { toFinalDecisionReasonSummaryData } from '../../../final-decision-reason-summary-data';

@Component({
  selector: 'cca-final-decision-reason-summary',
  template: `
    <div>
      <netz-page-heading data-testid="heading" [caption]="finalDecisionType | finalDecisionType">
        Summary
      </netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, FinalDecisionTypePipe, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly downloadUrl = generateDownloadUrl(this.taskId);

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
