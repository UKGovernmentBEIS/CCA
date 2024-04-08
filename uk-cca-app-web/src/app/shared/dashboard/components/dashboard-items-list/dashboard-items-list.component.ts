import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ItemTypePipe } from '@shared/dashboard/pipes';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukTableColumn, TableComponent, TagComponent } from 'govuk-components';

import { ItemDTO } from 'cca-api';

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
    TagComponent
  ]
})
export class DashboardItemsListComponent {
  @Input() items: ItemDTO[];
  @Input() tableColumns: GovukTableColumn<ItemDTO>[];
  @Input() unassignedLabel: string;
}
