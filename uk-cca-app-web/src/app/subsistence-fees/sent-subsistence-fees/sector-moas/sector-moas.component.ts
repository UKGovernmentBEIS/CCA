import { DecimalPipe } from '@angular/common';
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

import {
  SubsistenceFeesMoaSearchResultInfoDTO,
  SubsistenceFeesRunDetailsDTO,
  SubsistenceFeesRunInfoViewService,
} from 'cca-api';

import { SECTOR_MOAS_FORM, SectorMoasFormModel, SectorMoasFormProvider } from './sector-moas-form.provider';

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
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
    SubsistenceFeesRunPaymentStatusPipe,
  ],
  providers: [SectorMoasFormProvider, SubsistenceFeesRunPaymentStatusPipe, SubsistenceFeesRunMarkFacilitiesStatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorMoasComponent implements OnInit {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);
  private readonly destroyref = inject(DestroyRef);

  private readonly paymentStatusPipe = inject(SubsistenceFeesRunPaymentStatusPipe);
  private readonly markFacilitiesStatusPipe = inject(SubsistenceFeesRunMarkFacilitiesStatusPipe);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;

  readonly searchForm = inject<SectorMoasFormModel>(SECTOR_MOAS_FORM);

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
    { field: 'name', header: 'Sector' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  readonly state = signal<SectorMoasState>({
    subsistenceFeesMoas: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || 1,
    totalItems: 0,
    pageSize: 30,
  });

  readonly count = computed(() => this.state().totalItems);
  readonly currentPage = computed(() => this.state().currentPage);

  ngOnInit() {
    this.fetchMoas().subscribe();
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.fetchMoas().subscribe();
  }

  onClearFilters() {
    this.searchForm.reset();
    this.fetchMoas().subscribe();
  }

  handlePageChange(page: number) {
    if (page === this.state().currentPage) return;

    this.state.update((state) => ({
      ...state,
      currentPage: page,
    }));

    this.fetchMoas().subscribe();
  }

  private fetchMoas() {
    return this.subsistenceFeesRunInfoViewService
      .getSubsistenceFeesRunMoas(this.subFeesDetails.runId, {
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
