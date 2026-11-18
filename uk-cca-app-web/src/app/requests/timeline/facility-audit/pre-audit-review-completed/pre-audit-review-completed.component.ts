import { ChangeDetectionStrategy, Component } from '@angular/core';

import { TaskListComponent } from '@netz/common/components';

import { getPreAuditReviewSections } from './pre-audit-review-completed-task-content';

@Component({
  selector: 'cca-pre-audit-review-completed',
  template: `<netz-task-list [sections]="sections" />`,
  imports: [TaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewCompletedComponent {
  protected readonly sections = getPreAuditReviewSections();
}
