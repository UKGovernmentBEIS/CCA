import { DatePipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, switchMap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukTableColumn,
  GovukValidators,
  TableComponent,
  TagComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { CertificationPeriodViewInfoService, FacilityInfoViewService, type FacilitySearchResults } from 'cca-api';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

@Component({
  selector: 'cca-facilities-list',
  templateUrl: './facilities-list.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    TagComponent,
    ButtonDirective,
    TextInputComponent,
    PendingButtonDirective,
    PaginationComponent,
    TableComponent,
    StatusPipe,
    DatePipe,
    TitleCasePipe,
  ],
  providers: [DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilitiesListComponent {
  private readonly fb = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly facilityInfoViewService = inject(FacilityInfoViewService);
  private readonly certificationPeriodViewInfoService = inject(CertificationPeriodViewInfoService);
  private readonly authStore = inject(AuthStore);
  private readonly datePipe = inject(DatePipe);

  private readonly certificationPeriod = toSignal(
    this.certificationPeriodViewInfoService.getCurrentCertificationPeriod(),
  );

  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  protected readonly isAllowedUser = computed(() =>
    ['REGULATOR', 'OPERATOR'].includes(this.authStore.select(selectUserRoleType)()),
  );

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => {
    const period = this.certificationPeriod();
    const range =
      period?.startDate && period?.endDate
        ? `${this.datePipe.transform(period.startDate, 'd/M/yy')} – ${this.datePipe.transform(
            period.endDate,
            'd/M/yy',
          )}`
        : '';

    const fields = [
      { field: 'facilityBusinessId', header: 'ID' },
      { field: 'siteName', header: 'Site name', widthClass: 'govuk-!-width-one-third' },
      { field: 'status', header: 'Status' },
    ];

    if (this.isAllowedUser()) {
      fields.push({ field: 'auditRequired', header: 'Marked for audit' });
    }

    fields.push(
      { field: 'schemeExitDate', header: 'Scheme exit date' },
      {
        field: 'certificationStatus',
        header: `Certified status${range ? ` ${range}` : ''}`,
      },
    );

    return fields;
  });

  readonly form = this.fb.group({
    term: this.fb.control<string | null>(null, [
      GovukValidators.minLength(3, 'Enter at least 3 characters'),
      GovukValidators.maxLength(256, 'Enter up to 256 characters'),
    ]),
  });

  readonly currentPage = computed(() => {
    const params = this.queryParams();
    return +params?.get('page') || DEFAULT_PAGE;
  });

  readonly pageSize = computed(() => {
    const params = this.queryParams();
    return +params?.get('pageSize') || DEFAULT_PAGE_SIZE;
  });

  readonly searchTerm = computed(() => {
    const params = this.queryParams();
    return params?.get('term')?.trim() || null;
  });

  protected readonly facilitiesData = toSignal(
    this.activatedRoute.queryParamMap.pipe(
      map((params) => ({
        page: +params.get('page') || DEFAULT_PAGE,
        pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
        term: params.get('term')?.trim() || null,
      })),
      switchMap(({ page, pageSize, term }) => {
        this.form.get('term')?.setValue(term, { emitEvent: false });

        return this.facilityInfoViewService.searchFacilities(this.targetUnitId, page - 1, pageSize, term);
      }),
    ),
    { initialValue: { facilities: [], total: 0 } as FacilitySearchResults },
  );

  readonly state = computed(() => ({
    facilities: this.facilitiesData()?.facilities || [],
    totalItems: this.facilitiesData()?.total || 0,
  }));

  onSearch() {
    if (this.form.invalid) return;

    const term = this.form.value.term?.trim();
    this.handleQueryParamsNavigation({ term });
  }

  onPageChange(page: number): void {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ page: 1, pageSize });
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number; term: string }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'facilities',
    });
  }
}
