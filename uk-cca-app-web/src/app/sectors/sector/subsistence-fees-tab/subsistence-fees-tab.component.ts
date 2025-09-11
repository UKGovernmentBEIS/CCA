import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, startWith, Subject, switchMap, tap } from 'rxjs';

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
  SectorAssociationSubsistenceFeesService,
  SubsistenceFeesMoaSearchCriteria,
  SubsistenceFeesMoaSearchResultInfoDTO,
} from 'cca-api';

import {
  SECTOR_SUBSISTENCE_FEES_FORM,
  SectorSubsistenceFeesFormModel,
  SectorSubsistenceFeesFormProvider,
} from './subsistence-fees-form.provider';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

type SubsistenceFeesState = {
  subsistenceFeesMoas: SubsistenceFeesMoaSearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-subsistence-fees-tab',
  templateUrl: './subsistence-fees-tab.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DecimalPipe,
    DatePipe,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    SelectComponent,
    UtilityPanelComponent,
    TableComponent,
    PaginationComponent,
    TagComponent,
    StatusPipe,
    StatusColorPipe,
  ],
  providers: [SectorSubsistenceFeesFormProvider, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubsistenceFeesTabComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorAssociationSubsistenceFeesService = inject(SectorAssociationSubsistenceFeesService);
  private readonly statusPipe = inject(StatusPipe);

  private readonly searchSubmit = new Subject<void>();

  protected readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  readonly state = signal<SubsistenceFeesState>({
    subsistenceFeesMoas: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || DEFAULT_PAGE,
    pageSize: +this.activatedRoute.snapshot.paramMap.get('pageSize') || DEFAULT_PAGE_SIZE,
    totalItems: 0,
  });

  protected readonly count = computed(() => this.state().totalItems);
  protected readonly pageSize = computed(() => this.state().pageSize);
  protected readonly currentPage = computed(() => this.state().currentPage);

  protected readonly searchForm = inject<SectorSubsistenceFeesFormModel>(SECTOR_SUBSISTENCE_FEES_FORM);

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
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'submissionDate', header: 'Payment request date' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  constructor() {
    combineLatest([this.activatedRoute.queryParamMap, this.searchSubmit.pipe(startWith(null))])
      .pipe(
        takeUntilDestroyed(),
        switchMap(([queryParamMap]) => {
          const pageNumber = +queryParamMap.get('page') || DEFAULT_PAGE;
          const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

          this.state.update((state) => ({
            ...state,
            currentPage: pageNumber,
            pageSize,
          }));

          const searchCriteria: SubsistenceFeesMoaSearchCriteria = {
            moaType: 'SECTOR_MOA',
            pageNumber: pageNumber - 1,
            pageSize,
            markFacilitiesStatus: this.searchForm.value.markFacilitiesStatus,
            term: this.searchForm.value.term,
            paymentStatus: this.searchForm.value.paymentStatus,
          };

          return this.fetchSectorSubsistenceFeesMoas(searchCriteria);
        }),
      )
      .subscribe();
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.searchSubmit.next();
  }

  onClearFilters() {
    this.searchForm.reset();
    this.searchSubmit.next();
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  private fetchSectorSubsistenceFeesMoas(searchCriteria: SubsistenceFeesMoaSearchCriteria) {
    return this.sectorAssociationSubsistenceFeesService
      .getSectorSubsistenceFeesMoas(this.sectorId, searchCriteria)
      .pipe(
        tap((results) => {
          this.state.update((state) => ({
            ...state,
            subsistenceFeesMoas: results.subsistenceFeesMoas,
            totalItems: results.total,
          }));
        }),
      );
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'subsistence-fees',
    });
  }
}
