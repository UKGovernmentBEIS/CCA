import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DaysRemainingPipe, ItemLinkPipe, ItemNamePipe, UserFullNamePipe } from '@netz/common/pipes';
import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';

import { ItemDTO } from 'cca-api';

import { ItemTypePipe } from '../../pipes/item-type.pipe';

@Component({
  selector: 'cca-dashboard-items-list',
  templateUrl: './dashboard-items-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    TableComponent,
    RouterModule,
    ItemLinkPipe,
    ItemNamePipe,
    ItemTypePipe,
    UserFullNamePipe,
    TagComponent,
    DaysRemainingPipe,
  ],
})
export class DashboardItemsListComponent {
  items = input.required<ItemDTO[]>();
  tableColumns = input.required<GovukTableColumn<ItemDTO>[]>();
  unassignedLabel = input.required<string>();
}
