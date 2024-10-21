import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ItemLinkPipe, ItemNamePipe, UserFullNamePipe } from '@netz/common/pipes';
import { GovukTableColumn, LinkDirective, TableComponent, TagComponent } from '@netz/govuk-components';

import { ItemTargetUnitDTO } from 'cca-api';

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
    LinkDirective,
  ],
})
export class DashboardItemsListComponent {
  items = input.required<ItemTargetUnitDTO[]>();
  tableColumns = input.required<GovukTableColumn<ItemTargetUnitDTO>[]>();
  unassignedLabel = input.required<string>();
}
