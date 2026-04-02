import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { RequestTaskActionPayload } from 'cca-api';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NON_COMPLIANCE_DETAILS_SUBTASK, NonComplianceDetailsPayload } from '../types';
import { toNonComplianceSummaryData } from './check-your-answers-summary-data';

@Component({
  selector: 'cca-non-compliance-check-your-answers',
  template: `
    <netz-page-heading caption="Non-compliance details">Check your answers</netz-page-heading>
    <cca-summary [data]="data()" />
    @if (isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }
    <div class="govuk-!-margin-top-3">
      <netz-return-to-task-or-action-page />
    </div>
  `,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly nonComplianceDetails = this.requestTaskStore.select(
    nonComplianceDetailsQuery.selectNonComplianceDetails,
  );
  private readonly allRelevantWorkflows = this.requestTaskStore.select(
    nonComplianceDetailsQuery.selectAllRelevantWorkflows,
  );
  private readonly allRelevantFacilities = this.requestTaskStore.select(
    nonComplianceDetailsQuery.selectAllRelevantFacilities,
  );
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly data = computed(() =>
    toNonComplianceSummaryData(
      this.nonComplianceDetails(),
      this.allRelevantWorkflows() ?? {},
      this.allRelevantFacilities() ?? {},
      this.isEditable(),
    ),
  );

  onSubmit() {
    if (!this.isEditable()) {
      return;
    }

    const payload = this.requestTaskStore.select(
      nonComplianceDetailsQuery.selectPayload,
    )() as NonComplianceDetailsPayload;
    const currentSectionsCompleted = this.requestTaskStore.select(nonComplianceDetailsQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[NON_COMPLIANCE_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD',
        nonComplianceDetails: payload.nonComplianceDetails,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute });
    });
  }
}
