import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CorrectiveActionsSummaryDetailsComponent, TasksApiService } from '@requests/common';
import { produce } from 'immer';

import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload, CorrectiveAction } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';

@Component({
  selector: 'cca-corrective-actions-summary',
  template: `
    <netz-page-heading caption="Corrective actions">Summary</netz-page-heading>

    <cca-corrective-actions-summary-details
      [correctiveActions]="correctiveActions()"
      [isEditable]="isEditable()"
      (add)="onAddAction()"
      (remove)="onRemoveAction($event)"
    />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, CorrectiveActionsSummaryDetailsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CorrectiveActionsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly payload = this.requestTaskStore.select(auditDetailsCorrectiveActionsQuery.selectPayload);

  private readonly selectAuditDetailsAndCorrectiveActions = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions,
  );

  protected readonly correctiveActions = computed(
    () => this.selectAuditDetailsAndCorrectiveActions()?.correctiveActions,
  );

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly currentSectionsCompleted = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
  );

  private readonly requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  onRemoveAction(index: number) {
    const actions = produce(this.correctiveActions()?.actions, (draft) => draft.filter((_, i) => i !== index));
    const updatedPayload = update(this.payload(), actions);

    const dto = createRequestTaskActionProcessDTO(
      this.requestTaskId(),
      updatedPayload,
      this.currentSectionsCompleted(),
    );

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError(() => {
          throw new Error('Could not remove corrective action');
        }),
      )
      .subscribe();
  }

  onAddAction() {
    this.router.navigate(['../actions'], { relativeTo: this.activatedRoute });
  }
}

function update(
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  actions: CorrectiveAction[],
): AuditDetailsCorrectiveActionsSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.auditDetailsAndCorrectiveActions = {
      ...draft?.auditDetailsAndCorrectiveActions,
      correctiveActions: {
        ...draft?.auditDetailsAndCorrectiveActions?.correctiveActions,
        actions,
      },
    };
  });
}
