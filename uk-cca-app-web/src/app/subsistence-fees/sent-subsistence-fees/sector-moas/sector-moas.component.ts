import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TagComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent, UtilityPanelComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import {
  SubsistenceFeesMoaSearchCriteria,
  SubsistenceFeesMoaSearchResultInfoDTO,
  SubsistenceFeesMoaSearchResults,
  SubsistenceFeesRunInfoViewService,
} from 'cca-api';

import {
  INITIAL_FORM_VALUES,
  SECTOR_MOAS_FORM,
  SectorMoasFormModel,
  SectorMoasFormProvider,
} from './sector-moas-form.provider';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

type SectorMoasState = {
  subsistenceFeesMoas: SubsistenceFeesMoaSearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-sector-moas',
  templateUrl: './sector-moas.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DecimalPipe,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    SelectComponent,
    UtilityPanelComponent,
    TableComponent,
    PaginationComponent,
    TagComponent,
    StatusColorPipe,
    StatusPipe,
  ],
  providers: [SectorMoasFormProvider, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorMoasComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);
  private readonly statusPipe = inject(StatusPipe);

  private readonly runId = +this.activatedRoute.snapshot.paramMap.get('runId');

  readonly state = signal<SectorMoasState>({
    subsistenceFeesMoas: [],
    currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || DEFAULT_PAGE,
    pageSize: +this.activatedRoute.snapshot.queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  protected readonly subFeesDetails = toSignal(
    this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRunDetailsById(this.runId),
  );

  protected readonly searchForm = inject<SectorMoasFormModel>(SECTOR_MOAS_FORM);

  protected readonly paymentStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'AWAITING_PAYMENT', text: this.statusPipe.transform('AWAITING_PAYMENT') },
    { value: 'PAID', text: this.statusPipe.transform('PAID') },
    { value: 'OVERPAID', text: this.statusPipe.transform('OVERPAID') },
    { value: 'CANCELLED', text: this.statusPipe.transform('CANCELLED') },
  ];

  protected readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.statusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.statusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.statusPipe.transform('CANCELLED') },
  ];

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'transactionId', header: 'Transaction ID' },
    { field: 'name', header: 'Sector' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        tap((params) => {
          this.state.update((state) => ({
            ...state,
            currentPage: +params.get('page') || DEFAULT_PAGE,
            pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
          }));
        }),
        switchMap(() => this.fetchMoas()),
        tap((results: SubsistenceFeesMoaSearchResults) => this.updateState(results)),
      )
      .subscribe();
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: this.searchForm.value.term,
      paymentStatus: this.searchForm.value.paymentStatus,
      markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
    });
  }

  onClearFilters() {
    this.searchForm.reset();
    this.handleQueryParamsNavigation({
      page: DEFAULT_PAGE,
      term: INITIAL_FORM_VALUES.term,
      paymentStatus: INITIAL_FORM_VALUES.paymentStatus,
      markFacilitiesStatus: INITIAL_FORM_VALUES.markFacilitiesStatus,
    });
  }

  onPageChange(page: number) {
    if (page === this.state().currentPage) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.state().pageSize) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  private fetchMoas() {
    return this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRunMoas(this.runId, {
      moaType: 'SECTOR_MOA',
      pageNumber: this.state().currentPage - 1,
      pageSize: this.state().pageSize,
      term: this.searchForm.value.term,
      paymentStatus: this.searchForm.value.paymentStatus,
      markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
    });
  }

  private updateState(results: SubsistenceFeesMoaSearchResults) {
    this.state.update((state) => ({
      ...state,
      subsistenceFeesMoas: results.subsistenceFeesMoas,
      totalItems: results.total,
    }));
  }

  private handleQueryParamsNavigation(
    pagination: Partial<{
      page: number;
      pageSize: number;
      term: SubsistenceFeesMoaSearchCriteria['term'];
      paymentStatus: SubsistenceFeesMoaSearchCriteria['paymentStatus'];
      markFacilitiesStatus: SubsistenceFeesMoaSearchCriteria['markFacilitiesStatus'];
    }>,
  ) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'sector-moas',
    });
  }
}
