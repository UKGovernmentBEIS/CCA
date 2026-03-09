import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ItemsAssignedToOthersService } from 'cca-api';

import { DashboardFetchFn, DashboardTaskListComponent } from '../dashboard-task-list';

@Component({
  selector: 'cca-assigned-to-others',
  template: `<cca-dashboard-task-list heading="Assigned to others" [fetchFn]="fetchFn" />`,
  imports: [DashboardTaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignedToOthersComponent {
  private readonly service = inject(ItemsAssignedToOthersService);
  protected readonly fetchFn: DashboardFetchFn = (page, size) => this.service.getAssignedToOthersItems(page, size);
}
