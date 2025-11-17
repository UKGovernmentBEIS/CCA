import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';
import { FacilityItemViewModel } from '@requests/common';
import { StatusPipe } from '@shared/pipes';

@Component({
  selector: 'cca-facility-item-list',
  templateUrl: './facility-item-list.component.html',
  imports: [StatusPipe, TableComponent, RouterLink],
})
export class FacilityItemListComponent {
  protected readonly facilityItems = input.required<FacilityItemViewModel[]>();
  protected readonly isEditable = input(false);

  protected readonly sorting = signal<SortEvent>({ column: 'name', direction: 'ascending' });

  protected readonly sortedFacilityItems = computed(() =>
    this.facilityItems().slice().sort(this.onSort(this.sorting())),
  );

  protected readonly statusColumn = 'status';

  protected readonly tableColumns: GovukTableColumn[] = [
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
