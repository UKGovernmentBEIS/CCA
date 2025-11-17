import { DatePipe } from '@angular/common';
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
import {
  CcaTableColumn,
  PaginationComponent,
  SummaryComponent,
  TableComponent,
  UtilityPanelComponent,
} from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import {
  SubsistenceFeesMoaFacilitySearchResultInfoDTO,
  SubsistenceFeesMoaSearchCriteria,
  SubsistenceFeesRunDetailsDTO,
  SubsistenceFeesSearchCriteria,
} from 'cca-api';

import {
  INITIAL_FORM_VALUES,
  TARGET_UNIT_FACILITIES_LIST_FORM,
  TargetUnitFacilitiesListFormModel,
  TargetUnitFacilitiesListFormProvider,
} from './tu-facilities-list-form.provider';
import { TuMoaDetailsStore } from './tu-moa-details.store';
import { toTuMoaDetailsSummary } from './tu-moa-details-summary';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

@Component({
  selector: 'cca-tu-moa-details',
  templateUrl: './tu-moa-details.component.html',
  imports: [
    ReactiveFormsModule,
    TagComponent,
    PageHeadingComponent,
    StatusColorPipe,
    SummaryComponent,
    TableComponent,
    SelectComponent,
    TextInputComponent,
    PaginationComponent,
    ButtonDirective,
    PendingButtonDirective,
    UtilityPanelComponent,
    DatePipe,
    StatusPipe,
    RouterLink,
    WarningTextComponent,
  ],
  providers: [TargetUnitFacilitiesListFormProvider, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuMoaDetailsComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(TuMoaDetailsStore);
  private readonly statusPipe = inject(StatusPipe);

  protected readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;
  protected readonly moaId = +this.activatedRoute.snapshot.paramMap.get('moaId');

  readonly state = this.store.stateAsSignal;

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);

  protected readonly selectedFacilities = computed(() => this.state().selectedFacilities);

  protected readonly isReceivedAmountHigherThanTotal = computed(
    () => Number(this.state().moaTUDetails.receivedAmount) > Number(this.state().moaTUDetails.currentTotalAmount),
  );

  protected readonly summaryData = computed(() =>
    toTuMoaDetailsSummary(this.state().moaTUDetails, this.state().userRoleType === 'REGULATOR'),
  );

  protected readonly tableData = computed(() =>
    this.state().facilities.map((f) => ({
      ...f,
      isSelectable: f.markFacilitiesStatus !== 'CANCELLED',
    })),
  );

  protected readonly hasSelectableRows = computed(() => this.tableData().some((e) => e.isSelectable));

  protected readonly searchForm = inject<TargetUnitFacilitiesListFormModel>(TARGET_UNIT_FACILITIES_LIST_FORM);

  protected readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.statusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.statusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.statusPipe.transform('CANCELLED') },
  ];

  protected readonly tableColumns: CcaTableColumn[] = [
    { field: 'facilityBusinessId', header: 'Facility ID', primary: true },
    { field: 'facilityName', header: 'Site name' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'paymentDate', header: 'Payment date' },
    { field: 'markingHistory', header: 'Marking history' },
  ];

  constructor() {
    this.store
      .fetchTUMoaDetails(this.moaId)
      .pipe(
        switchMap(() => this.activatedRoute.queryParamMap),
        switchMap((queryParamMap) => {
          const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
          const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

          this.currentPage.set(pageNumber);
          this.pageSize.set(pageSize);

          const searchCriteria: SubsistenceFeesSearchCriteria = {
            term: this.searchForm.value.term,
            markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
            pageNumber: pageNumber - 1,
            pageSize,
          };

          return this.store.fetchTargetUnitFacilities(searchCriteria);
        }),
        this.store.updateFacilities(),
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

  onSelectChange({ row, checked }: { row: SubsistenceFeesMoaFacilitySearchResultInfoDTO; checked: boolean }) {
    this.store.updateSelectedTUMoAFacilities(row, checked);
  }

  onSelectWholePageChange(checked: boolean) {
    for (const f of this.state().facilities) {
      this.store.updateSelectedTUMoAFacilities(f, checked);
    }
  }

  onMark(path: 'paid' | 'in-progress' | 'cancelled') {
    if (this.selectedFacilities().size > 0) {
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
