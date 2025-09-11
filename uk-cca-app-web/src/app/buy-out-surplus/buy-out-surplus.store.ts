import { computed, effect, inject, Injectable } from '@angular/core';

import { EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { SignalStore } from '@netz/common/store';
import { HistoryCategory } from '@shared/types';

import { RequestDetailsDTO, RequestSearchCriteria, RequestsService } from 'cca-api';

export const DEFAULT_PAGE_SIZE = 50;
export const DEFAULT_PAGE = 1;

export type BuyoutSurplusState = {
  currentPage: number;
  runInProgress: boolean;
  badgeNumber: number;
  pageSize: number;
  workflowsHistory: RequestDetailsDTO[];
  totalWorkflowHistoryItems: number;
};

const INITIAL_STATE: BuyoutSurplusState = {
  currentPage: DEFAULT_PAGE,
  runInProgress: false,
  badgeNumber: 0,
  pageSize: DEFAULT_PAGE_SIZE,
  workflowsHistory: [],
  totalWorkflowHistoryItems: 0,
};

@Injectable()
export class BuyoutSurplusStore extends SignalStore<BuyoutSurplusState> {
  private readonly requestsService = inject(RequestsService);

  private readonly interval = 10000; // ms

  private readonly runInProgress = computed(() => this.stateAsSignal().runInProgress);

  constructor() {
    super(INITIAL_STATE);

    effect(() => {
      if (this.runInProgress()) this.initProgressUpdatePolling().subscribe();
    });
  }

  /**
   * Handles the polling mechanism.
   * If it finds an in progress request, the polling is initiated, until no in progress task is found.
   * It also updates the UI, by disabling the `New buy-out and surplus batch` button, showing a warning
   * and adding a badge number in the `Workflow history` tab.
   * @returns Itself if the polling is active or `EMPTY` when it's done.
   */
  initProgressUpdatePolling(): Observable<unknown> {
    return timer(this.interval).pipe(
      take(1),
      switchMap(() => this.checkForPendingBatchRun()),
      switchMap((requestInProgress) => {
        if (requestInProgress) return this.initProgressUpdatePolling();

        this.updateState({ badgeNumber: 0, runInProgress: false });
        this.fetchAndSetWorkflows();

        return EMPTY;
      }),
    );
  }

  checkForPendingBatchRun(): Observable<boolean> {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'CA',
      resourceId: 'ENGLAND',
      requestTypes: ['BUY_OUT_SURPLUS_RUN'],
      requestStatuses: ['IN_PROGRESS'],
      historyCategory: HistoryCategory.CA,
      pageNumber: 0,
      pageSize: this.state.pageSize || 10,
    };

    return this.fetchRequestDetails(requestSearchCriteria).pipe(
      map((results) => results.requestDetails),
      map((details) => details.some((item) => item.requestStatus === 'IN_PROGRESS')),
    );
  }

  /**
   * Fetches and updates the data in workflow history table.
   * It also notifies the parent to initiate the polling mechanism, if not already active,
   * if an entry is in progress.
   */
  fetchAndSetWorkflows() {
    this.fetchWorkflows()
      .pipe(take(1))
      .subscribe({
        next: (details) => {
          const isInProgress = details.requestDetails.some((item) => item.requestStatus === 'IN_PROGRESS');

          this.updateState({
            workflowsHistory: details.requestDetails,
            totalWorkflowHistoryItems: details.total,
            runInProgress: isInProgress,
            badgeNumber: isInProgress ? 1 : 0,
          });
        },
        error: (err) => {
          console.error('Error loading workflows', err);
        },
      });
  }

  private fetchWorkflows() {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'CA',
      resourceId: 'ENGLAND',
      requestTypes: ['BUY_OUT_SURPLUS_RUN'],
      historyCategory: HistoryCategory.CA,
      pageNumber: this.state.currentPage - 1,
      pageSize: this.state.pageSize || 10,
    };

    return this.fetchRequestDetails(requestSearchCriteria);
  }

  private fetchRequestDetails(requestSearchCriteria: RequestSearchCriteria) {
    return this.requestsService.getRequestDetailsByResource(requestSearchCriteria);
  }
}
