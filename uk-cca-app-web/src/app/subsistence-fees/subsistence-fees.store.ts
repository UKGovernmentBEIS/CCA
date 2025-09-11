import { computed, effect, inject, Injectable } from '@angular/core';

import { EMPTY, map, Observable, pipe, Subject, switchMap, take, takeUntil, tap, timer } from 'rxjs';

import { SignalStore } from '@netz/common/store';
import { ConfigService } from '@shared/config';
import { HistoryCategory } from '@shared/types';
import { produce } from 'immer';

import {
  RequestDetailsDTO,
  RequestDetailsSearchResults,
  RequestSearchCriteria,
  RequestsService,
  SubsistenceFeesRunInfoViewService,
  SubsistenceFeesRunSearchResultInfoDTO,
  SubsistenceFeesRunSearchResults,
} from 'cca-api';

/**
 * The logic of this store is to provide the same data & trigger changes
 * between the sent subsistence fees tab, the `Workflow history` tab, the `Sent subsistence fees` tab and their parent component, subsistence-fees-component.
 *
 * Right now, this behaviour is not described in either Confluence or Figma,
 * but it is required for the current screen.
 *
 * The expected behavior of the whole feature set can be broken down like this:
 * 1. The subsistence-fees-component (parent component) and checks if there are any in progress payment tasks.
 *   - If there are any, the button is disabled, a warning is displayed and the badge in the tab is shown. A polling starts to check for the completion of the task.
 *   - If there aren't any, we update the state to enable the button, remove the warning and trigger a data refetch in the `Workflow history` tab.
 * 2. Each tab re-fetches its data on navigate. If the workflow history tab fetches any in progress run, it needs to update the state to trigger the polling mechanism again.
 */

export type SubsistenceFeesState = {
  currentPage: number;
  pageSize: number;
  workflowsHistory: RequestDetailsDTO[];
  subsistenceFeesRuns: SubsistenceFeesRunSearchResultInfoDTO[];
  totalWorkflowHistoryItems: number;
  totalSubsistenceFeesRunItems: number;
  badgeNumber: number;
  isValidChargeDate: boolean;
  runInProgress: boolean;
};

const INITIAL_STATE: SubsistenceFeesState = {
  currentPage: 1,
  pageSize: 10,
  workflowsHistory: [],
  subsistenceFeesRuns: [],
  totalWorkflowHistoryItems: 0,
  totalSubsistenceFeesRunItems: 0,
  runInProgress: false,
  badgeNumber: 0,
  isValidChargeDate: undefined,
};

@Injectable()
export class SubsistenceFeesStore extends SignalStore<SubsistenceFeesState> {
  private readonly destroyRef = new Subject<void>();
  private readonly requestsService = inject(RequestsService);
  private readonly configService = inject(ConfigService);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);

  private readonly today = new Date();
  private readonly chargeInitDate = new Date(this.configService.getSubsistenceFeesRunTriggerDate());
  private readonly interval = 10000; // ms

  private readonly runInProgress = computed(() => this.stateAsSignal().runInProgress);

  constructor() {
    super(INITIAL_STATE);

    this.updateState({ isValidChargeDate: this.today >= this.chargeInitDate });

    effect(() => {
      if (this.runInProgress()) {
        this.initProgressUpdatePolling().subscribe();
      }
    });
  }

  updateState(state: Partial<SubsistenceFeesState>) {
    this.setState({ ...this.state, ...state });
  }

  /**
   * Handles the polling mechanism.
   * If it finds an in progress request, the polling is initiated, until no in progress task is found.
   * It also updates the UI, by disabling the `New payment request` button, showing a warning
   * and adding a badge number in the `Workflow history` tab.
   * @returns Itself if the polling is active or `EMPTY` when it's done.
   */
  initProgressUpdatePolling(): Observable<unknown> {
    return timer(this.interval).pipe(
      take(1),
      takeUntil(this.destroyRef),
      switchMap(() => this.checkForPendingSubsistenceRun()),
      switchMap((requestInProgress) => {
        if (requestInProgress) return this.initProgressUpdatePolling();

        this.updateState({ badgeNumber: 0, runInProgress: false });

        return this.fetchWorkflows();
      }),
      switchMap(() => this.fetchSubsistenceFeesRun()),
      this.updateSubsistenceFees(),
      map(() => EMPTY),
    );
  }

  checkForPendingSubsistenceRun(): Observable<boolean> {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'CA',
      resourceId: 'ENGLAND',
      requestTypes: ['SUBSISTENCE_FEES_RUN'],
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

  fetchSubsistenceFeesRun() {
    return this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRuns(
      this.state.currentPage - 1,
      this.state.pageSize,
    );
  }

  fetchWorkflows() {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'CA',
      resourceId: 'ENGLAND',
      requestTypes: ['SUBSISTENCE_FEES_RUN'],
      historyCategory: HistoryCategory.CA,
      pageNumber: this.state.currentPage - 1,
      pageSize: this.state.pageSize || 10,
    };

    return this.fetchRequestDetails(requestSearchCriteria);
  }

  updateSubsistenceFees() {
    return pipe(
      tap((results: SubsistenceFeesRunSearchResults) =>
        this.updateState({
          subsistenceFeesRuns: results.subsistenceFeesRuns,
          totalSubsistenceFeesRunItems: results.total,
        }),
      ),
    );
  }

  updateWorkflows() {
    return pipe(
      tap((results: RequestDetailsSearchResults) => {
        const isInProgress = results.requestDetails.some((item) => item.requestStatus === 'IN_PROGRESS');

        this.updateState({
          workflowsHistory: results.requestDetails,
          totalWorkflowHistoryItems: results.total,
          runInProgress: isInProgress,
          badgeNumber: isInProgress ? 1 : 0,
        });
      }),
    );
  }

  private fetchRequestDetails(requestSearchCriteria: RequestSearchCriteria) {
    return this.requestsService.getRequestDetailsByResource(requestSearchCriteria);
  }

  override reset() {
    this.destroyRef.next();
    this.updateState(
      produce(INITIAL_STATE, (s) => {
        delete s.isValidChargeDate;
      }),
    );
  }
}
