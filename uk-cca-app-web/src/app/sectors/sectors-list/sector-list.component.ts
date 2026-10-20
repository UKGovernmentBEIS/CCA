import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';

import { SectorAssociationInfoDTO, SectorAssociationInfoViewService } from 'cca-api';

@Component({
  selector: 'cca-sector-list',
  template: `
    <netz-page-heading size="xl">Manage Sectors</netz-page-heading>

    <govuk-table [columns]="tableColumns" [data]="sectors()" (sort)="sortBy($event)" data-testid="sector-list">
      <ng-template let-column="column" let-row="row">
        @if (column.field === 'sector') {
          <a [routerLink]="row.id" class="govuk-link">{{ row.sector }}</a>
        } @else {
          {{ row[column.field] }}
        }
      </ng-template>
    </govuk-table>
  `,
  imports: [PageHeadingComponent, TableComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorListComponent {
  private readonly sectorAssociationInfoViewService = inject(SectorAssociationInfoViewService);

  private readonly _sectors = toSignal(this.sectorAssociationInfoViewService.getSectorAssociations(), {
    initialValue: [],
  });

  protected readonly tableColumns: GovukTableColumn<SectorAssociationInfoDTO>[] = [
    { field: 'sector', header: 'Sector', isSortable: true },
    { field: 'mainContact', header: 'Main Contact', isSortable: true },
  ];

  protected readonly sorting = signal<SortEvent>({ column: 'sector', direction: 'ascending' });

  private readonly collator = new Intl.Collator('en-GB', {
    numeric: true,
    sensitivity: 'base',
  });

  protected readonly sectors = computed(() => {
    const { column, direction } = this.sorting();
    const multiplier = direction === 'ascending' ? 1 : -1;

    return [...this._sectors()].sort(
      (a, b) => this.collator.compare(String(a[column] ?? ''), String(b[column] ?? '')) * multiplier,
    );
  });

  protected sortBy(sort: SortEvent): void {
    this.sorting.set(sort);
  }
}
