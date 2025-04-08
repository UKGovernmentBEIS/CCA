import { DatePipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { CheckboxComponent, CheckboxesComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { RequestStatusTagColorPipe, WorkflowStatusPipe } from '@shared/pipes';
import { HistoryCategory } from '@shared/types';

import { RequestSearchCriteria, RequestsService } from 'cca-api';

import {
  originalOrder,
  WorkflowHistoryTabState,
  workflowStatusesMap,
  workflowTypesMap,
} from './workflow-history-tab.types';

@Component({
  selector: 'cca-workflow-history-tab',
  templateUrl: './workflow-history-tab.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CheckboxComponent,
    CheckboxesComponent,
    KeyValuePipe,
    DatePipe,
    PaginationComponent,
    RequestStatusTagColorPipe,
    WorkflowStatusPipe,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent implements OnInit {
  private readonly requestsService = inject(RequestsService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  private readonly targetUnitAccountId = this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  readonly state = signal<WorkflowHistoryTabState>({
    currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || 1,
    workflowsHistory: null,
    totalItems: 0,
    requestTypes: [],
    requestStatuses: [],
  });

  readonly pageSize = 30;
  readonly originalOrder = originalOrder;

  readonly searchForm = this.fb.group({
    requestTypes: this.fb.control<string[]>([]),
    requestStatuses: this.fb.control<string[]>([]),
  });

  readonly formData = toSignal(this.searchForm.valueChanges, { initialValue: this.searchForm.value });
  readonly currentPage = computed(() => this.state().currentPage);
  readonly workflowsHistory = computed(() => this.state().workflowsHistory?.requestDetails);
  readonly currentPage$ = toObservable(this.currentPage);
  readonly formData$ = toObservable(this.formData);

  readonly count = computed(() => this.state().totalItems);

  readonly workflowTypesMap = workflowTypesMap;
  readonly workflowStatusesMap = workflowStatusesMap;

  ngOnInit(): void {
    combineLatest([this.formData$, this.currentPage$]).subscribe(([formValues, page]) => {
      this.state.update((state) => ({
        ...state,
        requestTypes: formValues.requestTypes,
        requestStatuses: formValues.requestStatuses,
        currentPage: page,
      }));

      this.fetchAndSetWorkflows(this.targetUnitAccountId);
    });
  }

  private fetchAndSetWorkflows(targetUnitAccountId: string) {
    this.fetchWorkflows(targetUnitAccountId).subscribe({
      next: (data) => {
        this.state.update((state) => ({
          ...state,
          workflowsHistory: data.requestDetailsSearchResults,
          totalItems: data.requestDetailsSearchResults.total || 0,
        }));
      },
      error: (err) => {
        console.error('Error loading workflows', err);
      },
    });
  }

  private fetchWorkflows(targetUnitAccountId: string) {
    const requestSearchCriteria: RequestSearchCriteria = {
      resourceType: 'ACCOUNT', // Available options at NETZ's `ResourceType` and CCA's `CcaResourceType`
      resourceId: targetUnitAccountId,
      requestTypes: this.state().requestTypes,
      requestStatuses: this.state().requestStatuses,
      historyCategory: HistoryCategory.UNA,
      pageNumber: this.state().currentPage - 1,
      pageSize: this.pageSize,
    };

    return this.requestsService
      .getRequestDetailsByResource(requestSearchCriteria)
      .pipe(map((requestDetailsSearchResults) => ({ requestDetailsSearchResults })));
  }

  handlePageChange(page: number) {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }
}
