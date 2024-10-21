import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, LinkDirective, SortEvent, TableComponent } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';

import { SectorAssociationInfoDTO, SectorAssociationInfoViewService } from 'cca-api';

@Component({
  selector: 'cca-sector-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, TableComponent, RouterLink, LinkDirective],
  template: ` <cca-page-heading size="xl">Manage Sectors</cca-page-heading>
    <govuk-table [columns]="tableColumns" [data]="sectors()" (sort)="sortBy($event)" data-testid="sector-list">
      <ng-template let-column="column" let-index="index" let-row="row">
        @if (column.field === 'sector') {
          <a [routerLink]="row.id" govukLink>{{ row[column.field] }}</a>
        } @else {
          {{ row[column.field] }}
        }
      </ng-template>
    </govuk-table>`,
})
export class SectorListComponent {
  private readonly sectorAssociationInfoViewService = inject(SectorAssociationInfoViewService);

  private _sectors = toSignal(this.sectorAssociationInfoViewService.getSectorAssociations());

  tableColumns: GovukTableColumn[] = [
    { field: 'sector', header: 'Sector', isSortable: true },
    { field: 'mainContact', header: 'Main Contact', isSortable: true },
  ];

  sorting = signal<SortEvent | null>(null);

  sectors = computed(() => {
    const sorting = this.sorting();
    const sortingColumn = sorting?.column ?? this.tableColumns[0].field;
    const sectors: SectorAssociationInfoDTO[] = this._sectors();

    if (!sorting) return sectors;

    return sectors.sort((a, b) => {
      const diff = a[sortingColumn].localeCompare(b[sortingColumn], 'en-GB', { numeric: true, sensitivity: 'base' });
      return diff * (sorting?.direction === 'descending' ? -1 : 1);
    });
  });

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }
}
