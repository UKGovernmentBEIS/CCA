import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { tap } from 'rxjs';

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
import {
  SubsistenceFeesRunMarkFacilitiesStatusPipe,
  SubsistenceFeesRunPaymentStatusPipe,
  SubsistenceFeesRunPaymentStatusTagColorPipe,
} from '@shared/pipes';

import { SectorAssociationSubsistenceFeesService, SubsistenceFeesMoaSearchResultInfoDTO } from 'cca-api';

import {
  SECTOR_SUBSISTENCE_FEES_FORM,
  SectorSubsistenceFeesFormModel,
  SectorSubsistenceFeesFormProvider,
} from './subsistence-fees-form.provider';

type SibsistenceFeesState = {
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
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
    SubsistenceFeesRunPaymentStatusPipe,
  ],
  providers: [
    SectorSubsistenceFeesFormProvider,
    SubsistenceFeesRunPaymentStatusPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubsistenceFeesTabComponent implements OnInit {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorAssociationSubsistenceFeesService = inject(SectorAssociationSubsistenceFeesService);
  private readonly destroyref = inject(DestroyRef);

  private readonly paymentStatusPipe = inject(SubsistenceFeesRunPaymentStatusPipe);
  private readonly markFacilitiesStatusPipe = inject(SubsistenceFeesRunMarkFacilitiesStatusPipe);

  readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  readonly searchForm = inject<SectorSubsistenceFeesFormModel>(SECTOR_SUBSISTENCE_FEES_FORM);

  readonly paymentStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'AWAITING_PAYMENT', text: this.paymentStatusPipe.transform('AWAITING_PAYMENT') },
    { value: 'PAID', text: this.paymentStatusPipe.transform('PAID') },
    { value: 'OVERPAID', text: this.paymentStatusPipe.transform('OVERPAID') },
    { value: 'CANCELLED', text: this.paymentStatusPipe.transform('CANCELLED') },
  ];

  readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.markFacilitiesStatusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.markFacilitiesStatusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.markFacilitiesStatusPipe.transform('CANCELLED') },
  ];

  readonly tableColumns: GovukTableColumn[] = [
    { field: 'transactionId', header: 'Transaction ID' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'submissionDate', header: 'Payment request date' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  readonly state = signal<SibsistenceFeesState>({
    subsistenceFeesMoas: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || 1,
    totalItems: 0,
    pageSize: 30,
  });

  readonly count = computed(() => this.state().totalItems);
  readonly currentPage = computed(() => this.state().currentPage);

  ngOnInit(): void {
    this.fetchSectorSubsistenceFeesMoas().subscribe();
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.fetchSectorSubsistenceFeesMoas().subscribe();
  }

  onClearFilters() {
    this.searchForm.reset();
    this.fetchSectorSubsistenceFeesMoas().subscribe();
  }

  handlePageChange(page: number) {
    if (page === this.state().currentPage) return;

    this.state.update((state) => ({
      ...state,
      currentPage: page,
    }));

    this.fetchSectorSubsistenceFeesMoas().subscribe();
  }

  private fetchSectorSubsistenceFeesMoas() {
    return this.sectorAssociationSubsistenceFeesService
      .getSectorSubsistenceFeesMoas(this.sectorId, {
        moaType: 'SECTOR_MOA',
        pageNumber: this.state().currentPage - 1,
        pageSize: this.state().pageSize,
        ...this.searchForm.value,
      })
      .pipe(
        takeUntilDestroyed(this.destroyref),
        tap((results) => {
          this.state.update((state) => ({
            ...state,
            subsistenceFeesMoas: results.subsistenceFeesMoas,
            totalItems: results.total,
          }));
        }),
      );
  }
}
