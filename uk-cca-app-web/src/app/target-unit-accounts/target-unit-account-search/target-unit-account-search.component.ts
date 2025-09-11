import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, switchMap, tap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { AccountSearchResultInfoDTO, TargetUnitAccountInfoViewService } from 'cca-api';

import { TargetUnitAccountsListComponent } from '../target-unit-accounts-list/target-unit-accounts-list.component';

type SearchCriteria = {
  term: string | null;
  page: number;
  pageSize: number;
};

type AccountSearchState = {
  accounts: AccountSearchResultInfoDTO[];
  totalItems: number;
};

@Component({
  selector: 'cca-target-unit-account-search',
  templateUrl: './target-unit-account-search.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    ButtonDirective,
    TextInputComponent,
    PendingButtonDirective,
    TargetUnitAccountsListComponent,
    PaginationComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitAccountSearchComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);

  protected readonly state = signal<AccountSearchState>({
    accounts: [],
    totalItems: 0,
  });

  readonly currentPage = signal(1);
  readonly pageSize = signal(50);
  readonly accounts = computed(() => this.state().accounts);
  readonly count = computed(() => this.state().totalItems);

  readonly searchForm: FormGroup<{ term: FormControl<string | null> }> = this.fb.group({
    term: this.fb.control<string | null>(null, {
      validators: [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(256, 'Enter up to 256 characters'),
      ],
    }),
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        map((params) => ({
          term: params.get('term')?.trim() || null,
          page: +params.get('page') || this.currentPage(),
          pageSize: +params.get('pageSize') || this.pageSize(),
        })),
        tap(({ term, page, pageSize }) => {
          this.currentPage.set(page);
          this.pageSize.set(pageSize);
          this.searchForm.get('term')?.setValue(term);
        }),
        switchMap(({ term, page, pageSize }) =>
          this.targetUnitAccountInfoViewService.searchUserAccounts(page - 1, pageSize, term),
        ),
        tap(({ accounts, total }) => {
          this.state.set({
            accounts: accounts || [],
            totalItems: total || 0,
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
    });
  }
}
