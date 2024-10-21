import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  toVariationDetailsSummaryDataWithDecision,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-variation-details-check-your-answers',
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './variation-details-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toVariationDetailsSummaryDataWithDecision(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'))(),
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  onSubmit() {
    this.taskService
      .submitSubtask(VARIATION_DETAILS_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
