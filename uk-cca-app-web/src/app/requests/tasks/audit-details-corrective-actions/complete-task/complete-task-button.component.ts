import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { auditDetailsCorrectiveActionsQuery } from '../audit-details-corrective-actions.selectors';
import { AUDIT_DETAILS_SUBTASK, CORRECTIVE_ACTIONS_SUBTASK } from '../types';

@Component({
  selector: 'cca-complete-task',
  template: `
    @if (subtasksCompleted() && isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onCompleteTask()">Complete task</button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteTaskButtonComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly sectionsCompleted = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
  );

  readonly subtasksCompleted = computed(
    () =>
      this.sectionsCompleted()[AUDIT_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED &&
      this.sectionsCompleted()[CORRECTIVE_ACTIONS_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onCompleteTask() {
    this.router.navigate(['audit-details-corrective-actions', 'complete-task'], { relativeTo: this.activatedRoute });
  }
}
