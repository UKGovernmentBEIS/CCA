import { DatePipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukTableColumn,
  GovukValidators,
  SortEvent,
  TableComponent,
  TagComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { FacilityStatusPipe } from '@shared/pipes';

import { FacilityInfoViewService, FacilitySearchResultInfoDTO, FacilitySearchResults } from 'cca-api';

type State = {
  currentPage: number;
  facilities: FacilitySearchResultInfoDTO[];
  searchTerm: string;
  totalItems: number;
};

@Component({
  selector: 'cca-facilities-list',
  templateUrl: './facilities-list.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    TagComponent,
    ButtonDirective,
    TextInputComponent,
    PendingButtonDirective,
    PaginationComponent,
    TableComponent,
    FacilityStatusPipe,
    DatePipe,
    TitleCasePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilitiesListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly facilityInfoViewService = inject(FacilityInfoViewService);
  private readonly destroy$ = inject(DestroyRef);

  private readonly sorting = signal<SortEvent>({ column: 'id', direction: 'ascending' });
  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');
  private readonly page = +this.activatedRoute.snapshot.queryParamMap.get('page') || 1;

  private previousTerm: string | null = null;
  private previousPage: number | null = null;

  protected readonly pageSize = 30;

  protected readonly form = this.fb.group({
    term: this.fb.control<string | null>(null, [
      GovukValidators.minLength(3, 'Enter at least 3 characters'),
      GovukValidators.maxLength(256, 'Enter up to 256 characters'),
    ]),
  });

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'id', header: 'ID' },
    {
      field: 'siteName',
      header: 'Site name',
      widthClass: 'govuk-!-width-one-third',
    },
    { field: 'status', header: 'Status' },
    { field: 'schemeExitDate', header: 'Scheme exit date' },
    // TODO: check if the `Certified status` header can be populated with different types
    // of input, i.e. to contain a second line with a date range
    { field: 'certifiedStatus', header: 'Certified status' },
  ];

  protected readonly state = signal<State>({
    currentPage: this.page,
    facilities: [],
    searchTerm: '',
    totalItems: 0,
  });

  protected readonly currentPage = computed(() => this.state().currentPage);
  protected readonly count = computed(() => this.state().totalItems);

  protected readonly facilities = computed(() => {
    const facs = this.state()?.facilities;
    const sorting = this.sorting();

    if (!sorting) return facs;

    return facs?.sort((a, b) => {
      const diff = a[sorting.column].localeCompare(b[sorting.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  ngOnInit() {
    this.activatedRoute.queryParamMap
      .pipe(
        map((params) => ({
          term: params.get('term')?.trim() || null,
          page: +params.get('page') || 1,
          pageSize: this.pageSize,
        })),
        takeUntilDestroyed(this.destroy$),
      )
      .subscribe(({ term, page }) => {
        this.form.get('term').setValue(term, { emitEvent: false });
        this.state.update((state) => ({
          ...state,
          searchTerm: term,
          currentPage: page,
        }));

        if (this.previousPage !== page || term !== this.previousTerm) {
          this.fetchFacilities(term, page);
        }

        this.previousTerm = term;
        this.previousPage = page;
      });
  }

  onSearch() {
    if (this.form.invalid) return;

    const term = this.form.value.term || null;
    const page = this.state().currentPage;

    if (term === this.previousTerm && page === this.previousPage) return;

    // handles when someone searches and isn't on page 1.
    if (term !== this.previousTerm && page > 1) this.handlePageChange(1);

    this.router.navigate([], {
      queryParams: {
        term: term,
        page: null,
      },
      fragment: 'facilities',
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });

    this.fetchFacilities(term, this.state().currentPage);
    this.previousTerm = term;
    this.previousPage = page;
  }

  handlePageChange(page: number): void {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }

  private fetchFacilities(term: string, page: number) {
    this.facilityInfoViewService
      .searchFacilities(this.targetUnitId, page - 1, this.pageSize, term)
      .pipe(takeUntilDestroyed(this.destroy$))
      .subscribe(this.updateState);
  }

  private updateState = ({ facilities, total }: FacilitySearchResults): void => {
    this.state.update((state) => ({ ...state, facilities, totalItems: total }));
  };
}
