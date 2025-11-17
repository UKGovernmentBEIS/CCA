import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { SectorAssociationSchemeService, SubsectorAssociationInfoDTO } from 'cca-api';

import { toSectorSchemeSummaryData } from './scheme-summary-data';

@Component({
  selector: 'cca-sector-scheme-tab',
  templateUrl: './sector-scheme-tab.component.html',
  imports: [SummaryComponent, TableComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorSchemeTabComponent {
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  private readonly route = inject(ActivatedRoute);

  private readonly sectorScheme = toSignal(
    this.sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(
      +this.route.snapshot.paramMap.get('sectorId'),
    ),
  );

  private readonly sorting = signal<SortEvent | null>(null);

  protected readonly sectorCommitmentColumns: GovukTableColumn[] = [
    { field: 'targetPeriod', header: 'Target period', widthClass: 'govuk-!-width-one-third' },
    { field: 'targetImprovement', header: 'Target improvement' },
  ];

  protected readonly subsectorsColumns: GovukTableColumn[] = [{ field: 'name', header: 'Name', isSortable: true }];

  protected readonly sectorSchemeCCA2 = computed(
    () => this.sectorScheme()?.sectorAssociationSchemeMap?.[SchemeVersion.CCA_2],
  );
  protected readonly sectorCommitmentCCA2 = computed(() => this.sectorSchemeCCA2()?.targetSet?.targetCommitments);

  protected readonly sectorSchemeCCA3 = computed(
    () => this.sectorScheme()?.sectorAssociationSchemeMap?.[SchemeVersion.CCA_3],
  );
  protected readonly sectorCommitmentCCA3 = computed(() => this.sectorSchemeCCA3()?.targetSet?.targetCommitments);

  protected readonly subSectors = computed(() => {
    const sorting = this.sorting();
    const subSectors: SubsectorAssociationInfoDTO[] = this.sectorScheme()?.subsectorAssociations || [];

    if (!sorting) return subSectors;

    return subSectors.sort((a, b) => {
      const diff = a.name.localeCompare(b.name, 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }

  schemeSummaryData = computed(() => {
    const scheme = this.sectorScheme();
    return scheme ? toSectorSchemeSummaryData(scheme, this.subSectors().length) : [];
  });
}
