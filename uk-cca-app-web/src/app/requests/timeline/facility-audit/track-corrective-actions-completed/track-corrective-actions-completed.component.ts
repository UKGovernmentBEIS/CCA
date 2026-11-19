import { Component } from '@angular/core';

import { TaskListComponent } from '@netz/common/components';

import { getTrackCorrectiveActionsSections } from './track-corrective-actions-completed-task-content';

@Component({
  selector: 'cca-track-corrective-actions-completed',
  template: `<netz-task-list [sections]="sections" />`,
  imports: [TaskListComponent],
})
export class TrackCorrectiveActionsCompletedComponent {
  protected readonly sections = getTrackCorrectiveActionsSections();
}
