import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus } from '@requests/common';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NON_COMPLIANCE_DETAILS_SUBTASK } from '../types';

@Component({
  selector: 'cca-non-compliance-complete-task-button',
  template: `
    @if (subtaskCompleted() && isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onCompleteTask()">Complete task</button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceCompleteTaskButtonComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  private readonly sectionsCompleted = this.requestTaskStore.select(nonComplianceDetailsQuery.selectSectionsCompleted);

  protected readonly subtaskCompleted = computed(
    () => this.sectionsCompleted()?.[NON_COMPLIANCE_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED,
  );

  onCompleteTask() {
    this.router.navigate(['non-compliance', 'complete-task'], { relativeTo: this.activatedRoute });
  }
}
