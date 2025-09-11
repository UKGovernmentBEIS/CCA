import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toReviewTargetUnitDetailsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Change">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsCheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.route });
    });
  }
}
