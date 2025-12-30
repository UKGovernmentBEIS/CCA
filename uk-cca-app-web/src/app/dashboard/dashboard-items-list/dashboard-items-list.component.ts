import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DaysRemainingPipe, ItemLinkPipe, ItemNamePipe } from '@netz/common/pipes';
import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';

import { ItemDTO } from 'cca-api';

@Component({
  selector: 'cca-dashboard-items-list',
  templateUrl: './dashboard-items-list.component.html',
  imports: [TableComponent, RouterModule, ItemLinkPipe, ItemNamePipe, TagComponent, DaysRemainingPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardItemsListComponent {
  protected readonly items = input.required<ItemDTO[]>();
  protected readonly tableColumns = input.required<GovukTableColumn<ItemDTO>[]>();
}
