import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ItemsAssignedToMeService } from 'cca-api';

import { DashboardFetchFn, DashboardTaskListComponent } from '../dashboard-task-list';

@Component({
  selector: 'cca-assigned-to-me',
  template: `<cca-dashboard-task-list heading="Assigned to me" [fetchFn]="fetchFn" />`,
  imports: [DashboardTaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignedToMeComponent {
  private readonly service = inject(ItemsAssignedToMeService);
  protected readonly fetchFn: DashboardFetchFn = (page, size) => this.service.getAssignedItems(page, size);
}
