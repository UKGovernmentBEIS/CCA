import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from '../types';

@Component({
  selector: 'cca-conclusion-precontent',
  template: `
    @if (subtaskCompleted() && isEditable()) {
      @if (isWithdrawal()) {
        <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
          Notify Operator of decision
        </button>
      } @else {
        <button netzPendingButton govukButton type="button" (click)="onCompleteTask()">Complete task</button>
      }
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConclusionPrecontentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  private readonly sectionsCompleted = this.requestTaskStore.select(
    nonComplianceConclusionQuery.selectSectionsCompleted,
  );
  private readonly details = this.requestTaskStore.select(nonComplianceConclusionQuery.selectConclusionDetails);

  protected readonly subtaskCompleted = computed(
    () => this.sectionsCompleted()?.[NON_COMPLIANCE_CONCLUSION_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  protected readonly isWithdrawal = computed(() => this.details()?.penaltyOutcome === 'WITHDRAW');

  onNotifyOperatorOfDecision() {
    this.router.navigate(['non-compliance-conclusion', 'notify-operator'], { relativeTo: this.activatedRoute });
  }

  onCompleteTask() {
    this.router.navigate(['non-compliance-conclusion', 'complete-task'], { relativeTo: this.activatedRoute });
  }
}
