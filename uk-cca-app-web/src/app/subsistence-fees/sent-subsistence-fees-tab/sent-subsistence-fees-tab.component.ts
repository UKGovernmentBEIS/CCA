import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import {
  SubsistenceFeesRunMarkFacilitiesStatusPipe,
  SubsistenceFeesRunPaymentStatusPipe,
  SubsistenceFeesRunPaymentStatusTagColorPipe,
} from '@shared/pipes';

import { SubsistenceFeesStore } from '../subsistence-fees.store';

@Component({
  selector: 'cca-sent-subsistence-fees-tab',
  templateUrl: './sent-subsistence-fees-tab.component.html',
  standalone: true,

  imports: [
    RouterLink,
    DatePipe,
    DecimalPipe,
    TableComponent,
    TagComponent,
    PaginationComponent,
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
    SubsistenceFeesRunPaymentStatusPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SentSubsistenceFeesTabComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesStore = inject(SubsistenceFeesStore);

  readonly sentSubsistenceFeesColumns: GovukTableColumn[] = [
    { field: 'paymentRequestId', header: 'Payment request ID' },
    { field: 'submissionDate', header: 'Request date' },
    { field: 'paymentStatus', header: 'Payment status' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
    { field: 'outstandingTotalAmount', header: 'Outstanding (GBP)' },
  ];

  readonly state = computed(() => this.subsistenceFeesStore.stateAsSignal());

  ngOnInit() {
    this.subsistenceFeesStore.updateState({
      currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || 1,
    });

    this.subsistenceFeesStore.fetchAndSetSubsistenceFeesRun();
  }

  handlePageChange(page: number) {
    if (page === this.subsistenceFeesStore.stateAsSignal().currentPage) return;

    this.subsistenceFeesStore.updateState({
      currentPage: page,
    });

    this.subsistenceFeesStore.fetchAndSetSubsistenceFeesRun();
  }
}
