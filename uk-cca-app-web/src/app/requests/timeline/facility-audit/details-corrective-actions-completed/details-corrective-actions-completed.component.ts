import { ChangeDetectionStrategy, Component } from '@angular/core';

import { TaskListComponent } from '@netz/common/components';

import { getDetailsCorrectiveActionsSections } from './details-corrective-actions-completed-task-content';

@Component({
  selector: 'cca-details-corrective-actions-completed',
  template: `<netz-task-list [sections]="sections" />`,
  imports: [TaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsCorrectiveActionsCompletedComponent {
  protected readonly sections = getDetailsCorrectiveActionsSections();
}
