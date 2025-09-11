import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';

import { SectorAssociationInfoDTO, SectorAssociationInfoViewService } from 'cca-api';

@Component({
  selector: 'cca-sector-list',
  template: ` <netz-page-heading size="xl">Manage Sectors</netz-page-heading>
    <govuk-table [columns]="tableColumns" [data]="sectors()" (sort)="sortBy($event)" data-testid="sector-list">
      <ng-template let-column="column" let-index="index" let-row="row">
        @if (column.field === 'sector') {
          <a [routerLink]="row.id" class="govuk-link">{{ row[column.field] }}</a>
        } @else {
          {{ row[column.field] }}
        }
      </ng-template>
    </govuk-table>`,
  standalone: true,
  imports: [PageHeadingComponent, TableComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorListComponent {
  private readonly sectorAssociationInfoViewService = inject(SectorAssociationInfoViewService);

  private _sectors = toSignal(this.sectorAssociationInfoViewService.getSectorAssociations());

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'sector', header: 'Sector', isSortable: true },
    { field: 'mainContact', header: 'Main Contact', isSortable: true },
  ];

  protected readonly sorting = signal<SortEvent | null>(null);

  protected readonly sectors = computed(() => {
    const sorting = this.sorting();
    const sortingColumn = sorting?.column ?? this.tableColumns[0].field;
    const sectors: SectorAssociationInfoDTO[] = this._sectors();

    if (!sorting) return sectors;

    return sectors.slice().sort((a, b) => {
      const diff = a[sortingColumn].localeCompare(b[sortingColumn], 'en-GB', { numeric: true, sensitivity: 'base' });
      return diff * (sorting?.direction === 'descending' ? -1 : 1);
    });
  });

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }
}
