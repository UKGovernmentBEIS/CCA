import { DatePipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, of, switchMap } from 'rxjs';

import { ButtonDirective } from '@netz/govuk-components';

import { AccountNoteDto, AccountNoteResponse, AccountNotesService } from 'cca-api';

import { PaginationComponent } from '../pagination/pagination.component';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'cca-target-unit-notes',
  templateUrl: './target-unit-notes.component.html',
  imports: [DatePipe, KeyValuePipe, PaginationComponent, RouterLink, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitNotesComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly accountNotesService = inject(AccountNotesService);
  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  protected readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  readonly currentPage = computed(() => {
    const params = this.queryParams();
    return +params?.get('page') || DEFAULT_PAGE;
  });

  readonly pageSize = computed(() => {
    const params = this.queryParams();
    return +params?.get('pageSize') || DEFAULT_PAGE_SIZE;
  });

  readonly state = signal<{ notes: AccountNoteDto[]; totalItems: number }>({ notes: [], totalItems: 0 });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        switchMap((queryParamMap) => {
          const page = +queryParamMap.get('page') || DEFAULT_PAGE;
          const pageSize = +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE;

          const response$ = this.targetUnitId
            ? this.accountNotesService.getNotesByAccountId(this.targetUnitId, page - 1, pageSize)
            : of<AccountNoteResponse>({ accountNotes: [], totalItems: 0 });

          return response$.pipe(map((resp) => ({ notes: resp.accountNotes ?? [], totalItems: resp.totalItems ?? 0 })));
        }),
      )
      .subscribe((state) => this.state.set(state));
  }

  protected readonly notes = computed(() => this.state().notes);
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
