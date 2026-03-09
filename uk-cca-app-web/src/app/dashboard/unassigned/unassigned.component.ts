import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { UnassignedItemsService } from 'cca-api';

import { DashboardFetchFn, DashboardTaskListComponent } from '../dashboard-task-list';

@Component({
  selector: 'cca-unassigned',
  template: `<cca-dashboard-task-list heading="Unassigned" [fetchFn]="fetchFn" />`,
  imports: [DashboardTaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnassignedComponent {
  private readonly service = inject(UnassignedItemsService);
  protected readonly fetchFn: DashboardFetchFn = (page, size) => this.service.getUnassignedItems(page, size);
}
