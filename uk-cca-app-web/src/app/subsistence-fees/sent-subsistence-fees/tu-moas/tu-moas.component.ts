import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { tap } from 'rxjs';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
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

type TargetUnitMoasState = {
  subsistenceFeesMoas: SubsistenceFeesMoaSearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-tu-moas',
  templateUrl: './tu-moas.component.html',
  standalone: true,
  imports: [
    TableComponent,
    PaginationComponent,
    TagComponent,
    RouterLink,
    DecimalPipe,
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
    SubsistenceFeesRunPaymentStatusPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuMoasComponent implements OnInit {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);
  private readonly destroyref = inject(DestroyRef);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;

  readonly tableColumns: GovukTableColumn[] = [
    { field: 'transactionId', header: 'Transaction ID' },
    { field: 'businessId', header: 'Target unit ID' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  readonly state = signal<TargetUnitMoasState>({
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
        moaType: 'TARGET_UNIT_MOA',
        pageNumber: this.state().currentPage - 1,
        pageSize: this.state().pageSize,
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
