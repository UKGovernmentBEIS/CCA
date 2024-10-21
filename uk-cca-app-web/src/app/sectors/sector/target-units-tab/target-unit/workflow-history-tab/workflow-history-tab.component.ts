import { DatePipe, I18nSelectPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { CheckboxComponent, CheckboxesComponent, LinkDirective } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { RequestStatusTagColorPipe, WorkflowStatusPipe } from '@shared/pipes';
import { HistoryCategory } from '@shared/types';

import { RequestSearchByAccountCriteria, RequestsService } from 'cca-api';

import {
  originalOrder,
  WorkflowHistoryTabState,
  workflowStatusesMap,
  workflowTypesMap,
} from './workflow-history-tab.types';

@Component({
  selector: 'cca-workflow-history-tab',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CheckboxComponent,
    CheckboxesComponent,
    KeyValuePipe,
    LinkDirective,
    GovukDatePipe,
    I18nSelectPipe,
    DatePipe,
    PaginationComponent,
    RequestStatusTagColorPipe,
    WorkflowStatusPipe,
    RouterLink,
  ],
  templateUrl: './workflow-history-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryTabComponent implements OnInit {
  private readonly requestsService = inject(RequestsService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  private readonly targetUnitAccountId = this.route.snapshot.paramMap.get('targetUnitId');

  readonly state = signal<WorkflowHistoryTabState>({
    currentPage: +this.route.snapshot.queryParamMap.get('page') || 1,
    workflowsHistory: null,
    totalItems: 0,
    requestTypes: [],
    requestStatuses: [],
  });

  readonly pageSize = 30;
  readonly originalOrder = originalOrder;

  searchForm = this.fb.group({
    requestTypes: this.fb.control<string[]>([]),
    requestStatuses: this.fb.control<string[]>([]),
  });

  formData = toSignal(this.searchForm.valueChanges, { initialValue: this.searchForm.value });
  currentPage = computed(() => this.state().currentPage);
  workflowsHistory = computed(() => this.state().workflowsHistory?.requestDetails);
  currentPage$ = toObservable(this.currentPage);
  formData$ = toObservable(this.formData);

  count = computed(() => this.state().totalItems);

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
    const requestSearchByAccountCriteria: RequestSearchByAccountCriteria = {
      accountId: Number(targetUnitAccountId),
      requestTypes: this.state().requestTypes,
      requestStatuses: this.state().requestStatuses,
      historyCategory: HistoryCategory.UNA,
      pageNumber: this.state().currentPage - 1,
      pageSize: this.pageSize,
    };

    return this.requestsService
      .getRequestDetailsByAccountId(requestSearchByAccountCriteria)
      .pipe(map((requestDetailsSearchResults) => ({ requestDetailsSearchResults })));
  }

  handlePageChange(page: number) {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }
}
