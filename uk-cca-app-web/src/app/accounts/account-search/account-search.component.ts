import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { AccountSearchResultInfoDTO, TargetUnitAccountInfoViewService } from 'cca-api';

import { AccountsListComponent } from '../accounts-list/accounts-list.component';

type AccountSearchState = {
  accounts: AccountSearchResultInfoDTO[];
  searchTerm: string;
  totalItems: number;
  currentPage: number;
};

@Component({
  selector: 'cca-accounts-search',
  standalone: true,
  templateUrl: './account-search.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    ButtonDirective,
    TextInputComponent,
    PendingButtonDirective,
    AccountsListComponent,
    PaginationComponent,
  ],
})
export class AccountSearchComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);
  private readonly destroy$ = inject(DestroyRef);
  private previousTerm: string | null = null;
  private previousPage: number | null = null;

  readonly pageSize = 30;

  readonly state = signal<AccountSearchState>({
    accounts: [],
    currentPage: +this.route.snapshot.queryParamMap.get('page') || 1,
    searchTerm: null,
    totalItems: 0,
  });

  searchForm: FormGroup<{ term: FormControl<string | null> }> = this.fb.group({
    term: this.fb.control<string | null>(null, {
      validators: [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(256, 'Enter up to 256 characters'),
      ],
    }),
  });

  count = computed(() => this.state().totalItems);
  currentPage = computed(() => this.state().currentPage);

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(
        map((params) => ({
          term: params.get('term')?.trim() || null,
          page: +params.get('page') || 1,
          pageSize: this.pageSize,
        })),
        takeUntilDestroyed(this.destroy$),
      )
      .subscribe(({ term, page }) => {
        this.searchForm.get('term').setValue(term, { emitEvent: false });
        this.state.update((state) => ({ ...state, searchTerm: term, currentPage: page }));
        if (this.previousPage !== page || term !== this.previousTerm) {
          this.fetchAccounts(term, page);
        }
        this.previousTerm = term;
        this.previousPage = page;
      });
  }

  fetchAccounts(term: string, page: number): void {
    this.targetUnitAccountInfoViewService
      .searchUserAccounts(page - 1, this.pageSize, term)
      .pipe(takeUntilDestroyed(this.destroy$))
      .subscribe({
        next: ({ accounts, total }) => {
          this.state.update((state) => ({
            ...state,
            accounts: accounts,
            totalItems: total || 0,
          }));
        },
        error: (err) => console.error('Error fetching accounts:', err),
      });
  }

  onSearch(): void {
    if (this.searchForm.valid) {
      const term = this.searchForm.get('term').value || null;
      const page = this.state().currentPage;

      if (term === this.previousTerm && page === this.previousPage) {
        return;
      }

      // handles when someone searches and isn't on page 1.
      if (term !== this.previousTerm && page > 1) {
        this.handlePageChange(1);
      }

      this.router.navigate([], {
        queryParams: {
          term: term,
          page: null,
        },
        queryParamsHandling: 'merge',
        relativeTo: this.route,
      });

      this.fetchAccounts(term, this.state().currentPage);
      this.previousTerm = term;
      this.previousPage = page;
    }
  }

  handlePageChange(page: number): void {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }
}
