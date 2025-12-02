import { CommonModule, DatePipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, of, switchMap } from 'rxjs';

import { ButtonDirective } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { RequestNoteResponse, RequestNotesService } from 'cca-api';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-workflow-notes',
  imports: [CommonModule, DatePipe, KeyValuePipe, PaginationComponent, RouterLink, ButtonDirective],
  templateUrl: './notes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowNotesComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestNotesService = inject(RequestNotesService);
  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  protected readonly workflowId = this.activatedRoute.snapshot.paramMap.get('workflowId');

  readonly currentPage = computed(() => {
    const params = this.queryParams();
    return +params?.get('page') || DEFAULT_PAGE;
  });

  readonly pageSize = computed(() => {
    const params = this.queryParams();
    return +params?.get('pageSize') || DEFAULT_PAGE_SIZE;
  });

  private readonly state = toSignal(
    this.activatedRoute.queryParamMap.pipe(
      switchMap((queryParamMap) => {
        const page = +queryParamMap.get('page') || DEFAULT_PAGE;
        const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;
        const requestId = this.workflowId;

        const response$ = requestId
          ? this.requestNotesService.getNotesByRequestId(requestId, page - 1, pageSize)
          : of<RequestNoteResponse>({ requestNotes: [], totalItems: 0 });

        return response$.pipe(map((resp) => ({ notes: resp.requestNotes ?? [], totalItems: resp.totalItems ?? 0 })));
      }),
    ),
    { initialValue: { notes: [], totalItems: 0 } },
  );

  protected readonly notes = computed(() => this.state().notes);
  protected readonly totalItems = computed(() => this.state().totalItems);
  protected readonly hasNotes = computed(() => this.state().totalItems > 0);
  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'notes',
    });
  }
}
