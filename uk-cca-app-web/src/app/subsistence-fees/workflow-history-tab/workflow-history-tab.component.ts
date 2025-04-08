import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterModule } from '@angular/router';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { PaymentRequestProcessStatusPipe, PaymentRequestStatusTagColorPipe } from '@shared/pipes';

import { SubsistenceFeesStore } from '../subsistence-fees.store';

@Component({
  selector: 'cca-workflow-history-tab',
  templateUrl: './workflow-history-tab.component.html',
  standalone: true,
  imports: [
    RouterModule,
    RouterLink,
    DatePipe,
    TableComponent,
    TagComponent,
    PaginationComponent,
    PaymentRequestProcessStatusPipe,
    PaymentRequestStatusTagColorPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesStore = inject(SubsistenceFeesStore);

  readonly workflowHistoryColumns: GovukTableColumn[] = [
    { field: 'id', header: 'Payment request ID' },
    { field: 'creationDate', header: 'Date initiated' },
    { field: 'requestStatus', header: 'Process status', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'sentInvoices', header: 'Requests sent' },
    { field: 'failedInvoices', header: 'Failed requests' },
  ];

  readonly state = computed(() => this.subsistenceFeesStore.stateAsSignal());

  ngOnInit() {
    /**
     * Initiates the current page of the table data (if pagination is available).
     */
    this.subsistenceFeesStore.updateState({
      currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || 1,
    });

    this.subsistenceFeesStore.fetchAndSetWorkflows();
  }

  /**
   * It triggers a fetch request every time the page changes
   * @param page Table's page
   */
  handlePageChange(page: number) {
    if (page === this.subsistenceFeesStore.stateAsSignal().currentPage) return;

    this.subsistenceFeesStore.updateState({
      currentPage: page,
    });

    this.subsistenceFeesStore.fetchAndSetWorkflows();
  }
}
