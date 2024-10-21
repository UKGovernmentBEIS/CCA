import { DecimalPipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import {
  GovukTableColumn,
  SortEvent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';

import { SectorAssociationSchemeService, SubsectorAssociationSchemeInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-sector-scheme-tab',
  templateUrl: './sector-scheme-tab.component.html',
  standalone: true,
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TableComponent,
    RouterLink,
    DecimalPipe,
    PercentPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorSchemeTabComponent {
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  private readonly route = inject(ActivatedRoute);

  sectorScheme = toSignal(
    this.sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(
      +this.route.snapshot.paramMap.get('sectorId'),
    ),
  );

  sorting = signal<SortEvent | null>(null);

  sectorCommitmentColumns: GovukTableColumn[] = [
    { field: 'targetPeriod', header: 'Target period', widthClass: 'govuk-!-width-one-third' },
    { field: 'targetImprovement', header: 'Target improvement' },
  ];

  subsectorsColumns: GovukTableColumn[] = [{ field: 'name', header: 'Name', isSortable: true }];

  sectorCommitment = computed(() => this.sectorScheme().targetSet?.targetCommitments);

  subSectors = computed(() => {
    const sorting = this.sorting();
    const subSectors: SubsectorAssociationSchemeInfoDTO[] = this.sectorScheme().subsectorAssociationSchemes;

    if (!sorting) return subSectors;

    return subSectors.sort((a, b) => {
      const diff = a.subsectorAssociation[sorting.column].localeCompare(
        b.subsectorAssociation[sorting.column],
        'en-GB',
        {
          numeric: true,
          sensitivity: 'base',
        },
      );

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }
}
