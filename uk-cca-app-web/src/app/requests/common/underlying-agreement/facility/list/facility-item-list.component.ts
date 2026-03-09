import { DatePipe } from '@angular/common';
import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, SortEvent, TableComponent, TagComponent } from '@netz/govuk-components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { FacilityTimelineItemViewModel } from '../../types';

@Component({
  selector: 'cca-facility-item-list',
  templateUrl: './facility-item-list.component.html',
  imports: [DatePipe, StatusPipe, StatusColorPipe, TagComponent, TableComponent, RouterLink],
})
export class FacilityItemListComponent {
  protected readonly facilityItems = input.required<FacilityTimelineItemViewModel[]>();
  protected readonly isEditable = input(false);

  protected readonly sorting = signal<SortEvent>({ column: 'name', direction: 'ascending' });

  protected readonly sortedFacilityItems = computed(() =>
    this.facilityItems().slice().sort(this.onSort(this.sorting())),
  );

  protected readonly statusColumn = 'status';

  protected readonly tableColumns = computed(() => {
    const headers: GovukTableColumn[] = [
      { field: 'name', header: 'Name', isSortable: true },
      { field: 'facilityId', header: 'Facility ID', isSortable: true },
      { field: this.statusColumn, header: 'Status', isSortable: true },
    ];

    if (this.sortedFacilityItems().some((f) => f.decisionStatus)) {
      headers.push({ field: 'decisionStatus', header: 'Decision status', isSortable: true });
    }

    if (this.sortedFacilityItems().some((f) => f.chargeStartDate)) {
      headers.push({ field: 'chargeStartDate', header: 'Charge start date', isSortable: true });
    }

    return headers;
  });

  onSort(sortEvent: SortEvent): (fa: FacilityTimelineItemViewModel, fb: FacilityTimelineItemViewModel) => number {
    return (fa, fb) => {
      const diff: number = fa[sortEvent.column].localeCompare(fb[sortEvent.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sortEvent.direction === 'ascending' ? 1 : -1);
    };
  }
}
