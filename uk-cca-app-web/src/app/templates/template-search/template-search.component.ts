import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, switchMap, tap } from 'rxjs';

import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { DocumentTemplateDTO, NotificationTemplateDTO } from 'cca-api';

import { TemplateListComponent } from '../template-list/template-list.component';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

type SearchCriteria = {
  term: string | null;
  page: number;
  pageSize: number;
};

type TemplateSearchResults = {
  templates?: NotificationTemplateDTO[] | DocumentTemplateDTO[];
  total?: number;
};

export type TemplateSearchFetchFn = (
  page: number,
  pageSize: number,
  term: string | null,
) => Observable<TemplateSearchResults>;

@Component({
  selector: 'cca-template-search',
  templateUrl: './template-search.component.html',
  imports: [
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    PaginationComponent,
    TemplateListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateSearchComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly fetchFn = input.required<TemplateSearchFetchFn>();
  protected readonly templateType = input.required<'document' | 'email'>();
  protected readonly fragment = input.required<string>();

  protected readonly state = signal<{ templates: any[]; total: number }>({
    templates: [],
    total: 0,
  });

  protected readonly currentPage = signal(DEFAULT_PAGE);
  protected readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  protected readonly templates = computed(() => this.state().templates);
  protected readonly count = computed(() => this.state().total);

  protected readonly searchForm = new FormGroup<{ term: FormControl<string | null> }>({
    term: new FormControl<string | null>(null, {
      validators: [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(256, 'Enter up to 256 characters'),
      ],
    }),
  });

  constructor() {
    combineLatest([this.activatedRoute.queryParamMap, toObservable(this.fetchFn)])
      .pipe(
        takeUntilDestroyed(),
        map(([params, fetchFn]) => ({
          term: params.get('term')?.trim() || null,
          page: +params.get('page') || DEFAULT_PAGE,
          pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
          fetchFn,
        })),
        tap(({ term, page, pageSize }) => {
          this.currentPage.set(page);
          this.pageSize.set(pageSize);
          this.searchForm.get('term')?.setValue(term);
        }),
        switchMap(({ term, page, pageSize, fetchFn }) => fetchFn(page - 1, pageSize, term)),
        tap(({ templates, total }) => {
          this.state.set({
            templates: templates || [],
            total: total || 0,
          });
        }),
      )
      .subscribe();
  }

  onSearch() {
    if (!this.searchForm.valid) return;
    const term = this.searchForm.get('term')?.value?.trim() || null;
    this.handleQueryParamsNavigation({ term, page: 1 });
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(searchCriteria: Partial<SearchCriteria>) {
    this.router.navigate([], {
      queryParams: searchCriteria,
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.fragment(),
    });
  }
}
