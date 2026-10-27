import { Component, computed, effect, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';

import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent, UtilityPanelComponent } from '@shared/components';
import { DEFAULT_PAGE_SIZE, PAGE_SIZES } from '@shared/types';
import { facilityPerformanceReportStatusLabels } from '@shared/utils';

import {
  availableTargetPeriods,
  defaultFacilitiesListFilters,
  filterAndSortFacilities,
  getFacilitiesListPage,
  toFacilitiesListFilters,
} from './facilities-list';
import { FACILITIES_LIST_FORM_PROVIDER, FacilitiesListFormProvider } from './facilities-list-form.provider';
import { FacilitiesListRow, toFacilitiesListRow } from './facilities-list-row';
import { HistoricalFacility } from './historical-facilities';

@Component({
  selector: 'cca-buyout-surplus-facilities-list',
  templateUrl: './facilities-list.component.html',
  providers: [FacilitiesListFormProvider],
  imports: [
    ReactiveFormsModule,
    ButtonDirective,
    SelectComponent,
    TextInputComponent,
    TableComponent,
    PaginationComponent,
    UtilityPanelComponent,
    RouterLink,
  ],
})
export class FacilitiesListComponent {
  readonly facilities = input.required<HistoricalFacility[]>();
  // The account event enables navigation; each row also needs a supported API result.
  readonly facilityDetailsAvailable = input(false);

  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly queryParamMap = toSignal(this.activatedRoute.queryParamMap, {
    initialValue: this.activatedRoute.snapshot.queryParamMap,
  });

  protected readonly form = inject(FACILITIES_LIST_FORM_PROVIDER);
  private readonly identityCounts = computed(() => {
    const counts = new Map<string, number>();
    for (const facility of this.facilities()) {
      const key = `${facility.facilityBusinessId}/${facility.targetPeriod}`;
      counts.set(key, (counts.get(key) ?? 0) + 1);
    }
    return counts;
  });
  private readonly targetPeriods = computed(() => availableTargetPeriods(this.facilities()));
  private readonly filters = computed(() => toFacilitiesListFilters(this.queryParamMap(), this.targetPeriods()), {
    // Pagination changes must not repeat filtering and sorting.
    equal: (previous, current) =>
      previous.search === current.search &&
      previous.targetPeriod === current.targetPeriod &&
      previous.result === current.result,
  });
  private readonly filteredFacilities = computed(() => filterAndSortFacilities(this.facilities(), this.filters()));
  protected readonly page = computed(() => {
    const queryParamMap = this.queryParamMap();
    const currentPage = Number(queryParamMap.get('page'));
    const pageSize = PAGE_SIZES.find((size) => size === Number(queryParamMap.get('pageSize'))) ?? DEFAULT_PAGE_SIZE;
    return getFacilitiesListPage(this.filteredFacilities(), currentPage, pageSize);
  });
  protected readonly rows = computed(() =>
    this.page().entries.map((facility) => {
      const row = toFacilitiesListRow(facility);
      row.detailsAvailable &&=
        this.identityCounts().get(`${facility.facilityBusinessId}/${facility.targetPeriod}`) === 1;
      return row;
    }),
  );
  protected readonly targetPeriodOptions = computed<GovukSelectOption[]>(() => [
    { text: 'All', value: null },
    // Keep every processed period available even when filters hide all its rows.
    ...this.targetPeriods().map((targetPeriod) => ({ text: targetPeriod, value: targetPeriod })),
  ]);
  protected readonly resultOptions: GovukSelectOption[] = [
    { text: 'All', value: null },
    { text: facilityPerformanceReportStatusLabels.TARGET_MET, value: 'TARGET_MET' },
    { text: facilityPerformanceReportStatusLabels.TARGET_NOT_MET, value: 'TARGET_NOT_MET' },
  ];
  protected readonly columns: GovukTableColumn<FacilitiesListRow>[] = [
    { field: 'siteName', header: 'Site name' },
    { field: 'facilityBusinessId', header: 'Facility ID' },
    { field: 'targetPeriod', header: 'TP' },
    { field: 'result', header: 'TPR Result' },
    { field: 'surplusUsed', header: 'Surplus used (tCO₂e)', isNumeric: true },
    { field: 'buyoutAmount', header: 'Buy-out amount (GBP)', isNumeric: true },
    { field: 'refundAmount', header: 'Overpayment buy-out amount (GBP)', isNumeric: true },
    { field: 'surplusGained', header: 'Surplus gained (tCO₂e)', isNumeric: true },
  ];

  constructor() {
    effect(() => {
      this.form.patchValue(this.filters(), { emitEvent: false });
    });
  }

  onApplyFilters(): void {
    const filters = this.form.getRawValue();
    this.handleQueryParamsNavigation({ ...filters, search: filters.search || null, page: 1 });
  }

  onClearFilters(): void {
    this.form.reset(defaultFacilitiesListFilters(), { emitEvent: false });
    this.handleQueryParamsNavigation({ search: null, targetPeriod: null, result: null, page: 1 });
  }

  onPageChange(page: number): void {
    if (page === this.page().currentPage) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number): void {
    if (pageSize === this.page().pageSize) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(queryParams: Params): void {
    this.router.navigate([], {
      queryParams,
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      preserveFragment: true,
    });
  }
}
