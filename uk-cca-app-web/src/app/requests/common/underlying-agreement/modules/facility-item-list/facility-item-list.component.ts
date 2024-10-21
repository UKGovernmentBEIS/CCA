import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, LinkDirective, SortEvent, TableComponent } from '@netz/govuk-components';

import { FacilityStatusPipe } from '../../pipes';
import { FacilityItemViewModel } from '../../underlying-agreement.types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-facility-item-list',
  templateUrl: './facility-item-list.component.html',
  standalone: true,
  imports: [FacilityStatusPipe, LinkDirective, TableComponent, RouterLink],
})
export class FacilityItemListComponent {
  facilityItems = input.required<FacilityItemViewModel[]>();
  isEditable = input(false);

  sorting = signal<SortEvent>({ column: 'name', direction: 'ascending' });

  sortedFacilityItems = computed(() => {
    return this.facilityItems().slice().sort(this.onSort(this.sorting()));
  });

  statusColumn = 'status';

  tableColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'facilityId', header: 'Facility ID', isSortable: true },
    { field: this.statusColumn, header: 'Status', isSortable: true },
    { field: 'links', header: 'Actions' },
  ];

  onSort(sortEvent: SortEvent): (fa: FacilityItemViewModel, fb: FacilityItemViewModel) => number {
    return (fa, fb) => {
      const diff: number = fa[sortEvent.column].localeCompare(fb[sortEvent.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sortEvent.direction === 'ascending' ? 1 : -1);
    };
  }
}
