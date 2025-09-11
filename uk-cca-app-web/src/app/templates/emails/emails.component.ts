import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, switchMap, tap } from 'rxjs';

import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import { NotificationTemplateSearchResults, NotificationTemplatesService } from 'cca-api';

import { TemplateListComponent } from '../template-list/template-list.component';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 30;

type SearchCriteria = {
  term: string | null;
  page: number;
  pageSize: number;
};

@Component({
  selector: 'cca-emails',
  templateUrl: './emails.component.html',
  standalone: true,
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
export class EmailsComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly notificationTemplatesService = inject(NotificationTemplatesService);

  protected readonly state = signal<NotificationTemplateSearchResults>({
    templates: [],
    total: 0,
  });

  readonly currentPage = signal(DEFAULT_PAGE);
  readonly pageSize = signal(DEFAULT_PAGE_SIZE);
  readonly templates = computed(() => this.state().templates);
  readonly count = computed(() => this.state().total);

  readonly searchForm = new FormGroup<{ term: FormControl<string | null> }>({
    term: new FormControl<string | null>(null, {
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
          page: +params.get('page') || DEFAULT_PAGE,
          pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
        })),
        tap(({ term, page, pageSize }) => {
          this.currentPage.set(page);
          this.pageSize.set(pageSize);
          this.searchForm.get('term')?.setValue(term);
        }),
        switchMap(({ term, page, pageSize }) =>
          this.notificationTemplatesService.getCurrentUserNotificationTemplates(page - 1, pageSize, [], term),
        ),
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
      fragment: 'emails',
    });
  }
}
