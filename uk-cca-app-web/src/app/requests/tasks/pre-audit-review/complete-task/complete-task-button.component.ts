import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { preAuditReviewQuery } from '../pre-audit-review.selectors';
import {
  PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK,
  PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK,
  PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK,
} from '../types';

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

  private readonly preAuditReviewSectionsCompleted = this.requestTaskStore.select(
    preAuditReviewQuery.selectSectionsCompleted,
  );

  readonly subtasksCompleted = computed(
    () =>
      this.preAuditReviewSectionsCompleted()[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] === TaskItemStatus.COMPLETED &&
      this.preAuditReviewSectionsCompleted()[PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK] ===
        TaskItemStatus.COMPLETED &&
      this.preAuditReviewSectionsCompleted()[PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onCompleteTask() {
    this.router.navigate(['pre-audit-review', 'complete-task'], { relativeTo: this.activatedRoute });
  }
}
