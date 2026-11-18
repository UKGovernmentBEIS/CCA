import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { trackCorrectiveActionsQuery } from '../track-corrective-actions.selectors';

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
    trackCorrectiveActionsQuery.selectSectionsCompleted,
  );

  private readonly correctiveActionResponses = this.requestTaskStore.select(
    trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions,
  )()?.correctiveActionResponses;

  readonly subtasksCompleted = computed(
    () =>
      Object.values(this.sectionsCompleted()).every((s) => s === TaskItemStatus.COMPLETED) &&
      Object.values(this.sectionsCompleted()).length === Object.values(this.correctiveActionResponses).length,
  );

  onCompleteTask() {
    this.router.navigate(['track-corrective-actions', 'complete-task'], { relativeTo: this.activatedRoute });
  }
}
