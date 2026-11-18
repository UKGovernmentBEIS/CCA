import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { switchMap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukSelectOption,
  SelectComponent,
  TagComponent,
  TextInputComponent,
  WarningTextComponent,
} from '@netz/govuk-components';
import { PaginationComponent, SummaryComponent, TableComponent, UtilityPanelComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { SubsistenceFeesMoaSearchCriteria, SubsistenceFeesMoaTargetUnitSearchResultInfoDTO } from 'cca-api';

import { CcaTableColumn } from 'src/app/shared/components/table/types';

import { SectorMoaDetailsStore } from './sector-moa-details.store';
import { toSectorMoaDetailsSummary } from './sector-moa-details-summary';
import {
  INITIAL_FORM_VALUES,
  TARGET_UNITS_LIST_FORM,
  TargetUnitsListFormModel,
  TargetUnitsListFormProvider,
} from './tu-list-form.provider';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

@Component({
  selector: 'cca-sector-moa-details',
  templateUrl: './sector-moa-details.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    TableComponent,
    TagComponent,
    PaginationComponent,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    SelectComponent,
    PageHeadingComponent,
    StatusColorPipe,
    StatusPipe,
    DecimalPipe,
    SummaryComponent,
    WarningTextComponent,
    UtilityPanelComponent,
  ],
  providers: [TargetUnitsListFormProvider, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorMoaDetailsComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(SectorMoaDetailsStore);
  private readonly statusPipe = inject(StatusPipe);

  private readonly moaId = +this.activatedRoute.snapshot.paramMap.get('moaId');

  readonly state = this.store.stateAsSignal;

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);

  protected readonly selectedTUs = computed(() => this.state().selectedTUs);

  protected readonly isReceivedAmountHigherThanTotal = computed(
    () =>
      Number(this.state().sectorMoaDetails.receivedAmount) > Number(this.state().sectorMoaDetails.currentTotalAmount),
  );

  protected readonly summaryData = computed(() =>
    toSectorMoaDetailsSummary(this.state().sectorMoaDetails, this.store.stateAsSignal().userRoleType === 'REGULATOR'),
  );

  protected readonly tableData = computed(() =>
    this.state().targetUnits.map((tu) => ({
      ...tu,
      isSelectable: tu.markFacilitiesStatus !== 'CANCELLED',
    })),
  );

  protected readonly hasSelectableRows = computed(() => this.tableData().some((e) => e.isSelectable));

  protected readonly searchForm = inject<TargetUnitsListFormModel>(TARGET_UNITS_LIST_FORM);

  protected readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.statusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.statusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.statusPipe.transform('CANCELLED') },
  ];

  protected readonly tableColumns: CcaTableColumn[] = [
    { field: 'businessId', header: 'Target unit ID', primary: true },
    { field: 'name', header: 'Operator' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
  ];

  constructor() {
    this.store
      .fetchSectorMoaDetails(this.moaId)
      .pipe(
        switchMap(() => this.activatedRoute.queryParamMap),
        switchMap((queryParamMap) => {
          const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
          const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

          this.currentPage.set(pageNumber);
          this.pageSize.set(pageSize);

          const searchCriteria: SubsistenceFeesMoaSearchCriteria = {
            term: this.searchForm.value.term,
            markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
            pageNumber: pageNumber - 1,
            pageSize,
            moaType: 'SECTOR_MOA',
          };

          return this.store.fetchTargetUnits(searchCriteria);
        }),
        this.store.updateTargetUnits(),
      )
      .subscribe();
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: this.searchForm.value.term,
      markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
    });
  }

  onClearFilters() {
    this.searchForm.reset();
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: INITIAL_FORM_VALUES.term,
      markFacilitiesStatus: INITIAL_FORM_VALUES.markFacilitiesStatus,
    });
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  onSelectChange({ row, checked }: { row: SubsistenceFeesMoaTargetUnitSearchResultInfoDTO; checked: boolean }) {
    this.store.updateSelectedTUs(row, checked);
  }

  onSelectWholePageChange(checked: boolean) {
    for (const tu of this.state().targetUnits) {
      this.store.updateSelectedTUs(tu, checked);
    }
  }

  onMark(path: 'paid' | 'in-progress' | 'cancelled') {
    if (this.selectedTUs().size > 0) {
      this.router.navigate(['mark-facilities', path], { relativeTo: this.activatedRoute });
    }
  }

  private handleQueryParamsNavigation(
    filters: Partial<{
      page: number;
      pageSize: number;
      term: SubsistenceFeesMoaSearchCriteria['term'];
      markFacilitiesStatus: SubsistenceFeesMoaSearchCriteria['markFacilitiesStatus'];
    }>,
  ) {
    this.router.navigate([], {
      queryParams: { ...filters },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'sector-moas',
    });
  }
}
