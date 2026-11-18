import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { Cca3FacilityMigrationData } from 'cca-api';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-migrated-facilities-list',
  templateUrl: './migrated-facilities-list.component.html',
  imports: [ReactiveFormsModule, RouterLink, PaginationComponent, TableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MigratedFacilitiesListComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly migratedFacilities = input.required<Cca3FacilityMigrationData[]>();

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  protected readonly sorting = signal<SortEvent>({ column: 'facilityName', direction: 'ascending' });

  private readonly mappedFacilities = computed(() =>
    this.migratedFacilities().map((facility) => ({
      ...facility,
      participatingInCca3Scheme: facility.participatingInCca3Scheme ? 'Both' : 'CCA2',
    })),
  );

  protected readonly sortedFacilityItems = computed(() =>
    this.mappedFacilities().slice().sort(this.onSort(this.sorting())),
  );

  protected readonly paginatedItems = computed(() => {
    const sorted = this.sortedFacilityItems();
    const startIndex = (this.currentPage() - 1) * this.pageSize();
    const endIndex = startIndex + this.pageSize();
    return sorted.slice(startIndex, endIndex);
  });

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'facilityName', header: 'Site name' },
    { field: 'facilityBusinessId', header: 'Facility ID' },
    { field: 'participatingInCca3Scheme', header: 'Scheme participation' },
  ];

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
      const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

      this.currentPage.set(pageNumber);
      this.pageSize.set(pageSize);
    });
  }

  onSort<T>(sortEvent: SortEvent): (fa: T, fb: T) => number {
    return (fa, fb) => {
      const diff: number = fa[sortEvent.column].localeCompare(fb[sortEvent.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sortEvent.direction === 'ascending' ? 1 : -1);
    };
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(params: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...params },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }
}
