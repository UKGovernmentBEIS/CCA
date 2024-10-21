import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  toReviewTargetUnitDetailsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { PageHeadingComponent } from '@shared/components';
import { SummaryComponent } from '@shared/components/summary';
import { PendingButtonDirective } from '@shared/directives';

@Component({
  selector: 'cca-check-your-answers',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './review-target-unit-details-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    this.taskService
      .submitSubtask(REVIEW_TARGET_UNIT_DETAILS_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
