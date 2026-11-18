import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { adminTerminationFinalDecisionQuery } from '../admin-termination-final-decision.selectors';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../types';

@Component({
  selector: 'cca-admin-termination-final-decision-precontent',
  template: `
    @if (isCompleted() && isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Notify operator of decision</button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationFinalDecisionPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly sectionsCompleted = this.requestTaskStore.select(
    adminTerminationFinalDecisionQuery.selectSectionsCompleted,
  );

  protected readonly isCompleted = computed(
    () => this.sectionsCompleted()[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  onSubmit() {
    this.router.navigate(['admin-termination-final-decision', 'final-decision-notify-operator'], {
      relativeTo: this.activatedRoute,
      replaceUrl: true,
    });
  }
}
