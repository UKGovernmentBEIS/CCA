import { DatePipe } from '@angular/common';
import {
  AfterViewChecked,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { switchMap, tap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { BreadcrumbService } from '@netz/common/navigation';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TagComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent, SummaryComponent, SummaryData, UtilityPanelComponent } from '@shared/components';
import {
  SubsistenceFeesRunMarkFacilitiesStatusPipe,
  SubsistenceFeesRunPaymentStatusPipe,
  SubsistenceFeesRunPaymentStatusTagColorPipe,
} from '@shared/pipes';

import {
  SubsistenceFeesMoaDetailsDTO,
  SubsistenceFeesMoaFacilitySearchResultInfoDTO,
  SubsistenceFeesMoAInfoViewService,
  SubsistenceFeesMoATargetUnitInfoViewService,
  SubsistenceFeesRunDetailsDTO,
  SubsistenceFeesSearchCriteria,
} from 'cca-api';

import {
  TARGET_UNIT_FACILITIES_LIST_FORM,
  TargetUnitFacilitiesListFormModel,
  TargetUnitFacilitiesListFormProvider,
} from './tu-facilities-list-form.provider';
import { toTuMoaDetailsSummary } from './tu-moa-details-summary';

type TargetUnitFacilitiesListState = {
  facilities: SubsistenceFeesMoaFacilitySearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-tu-moa-details',
  templateUrl: './tu-moa-details.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    TagComponent,
    PageHeadingComponent,
    SubsistenceFeesRunPaymentStatusPipe,
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SummaryComponent,
    TableComponent,
    SelectComponent,
    TextInputComponent,
    PaginationComponent,
    ButtonDirective,
    PendingButtonDirective,
    UtilityPanelComponent,
    DatePipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
  ],
  providers: [TargetUnitFacilitiesListFormProvider, SubsistenceFeesRunMarkFacilitiesStatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuMoaDetailsComponent implements OnInit, AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyref = inject(DestroyRef);
  private readonly subsistenceFeesMoAInfoViewService = inject(SubsistenceFeesMoAInfoViewService);
  private readonly subsistenceFeesMoATargetUnitInfoViewService = inject(SubsistenceFeesMoATargetUnitInfoViewService);
  private readonly breadcrumbService = inject(BreadcrumbService);
  private readonly markFacilitiesStatusPipe = inject(SubsistenceFeesRunMarkFacilitiesStatusPipe);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;
  readonly moaId = +this.activatedRoute.snapshot.paramMap.get('moaId');

  moaDetails: SubsistenceFeesMoaDetailsDTO = null;
  data: SummaryData = null;

  readonly state = signal<TargetUnitFacilitiesListState>({
    facilities: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || 1,
    totalItems: 0,
    pageSize: 30,
  });

  readonly searchForm = inject<TargetUnitFacilitiesListFormModel>(TARGET_UNIT_FACILITIES_LIST_FORM);

  readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.markFacilitiesStatusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.markFacilitiesStatusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.markFacilitiesStatusPipe.transform('CANCELLED') },
  ];

  readonly tableColumns: GovukTableColumn[] = [
    { field: 'facilityId', header: 'Facility ID' },
    { field: 'facilityName', header: 'Site name' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'paymentDate', header: 'Payment date' },
  ];

  ngOnInit() {
    this.fetchDetails().subscribe();
  }

  ngAfterViewChecked(): void {
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Subsistence fees',
        link: ['/', 'subsistence-fees'],
        fragment: 'sent-subsistence-fees',
      },
      {
        text: `${this.subFeesDetails.paymentRequestId}: Target unit MoAs`,
        link: ['/', 'subsistence-fees', 'sent-subsistence-fees', `${this.subFeesDetails.runId}`],
        fragment: 'tu-moas',
      },
    ]);
  }

  onApplyFilters() {
    if (this.searchForm.invalid) return;
    this.fetchTargetUnitFacilities().subscribe();
  }

  onClearFilters() {
    this.searchForm.reset();
    this.fetchTargetUnitFacilities().subscribe();
  }

  handlePageChange(page: number) {
    if (page === this.state().currentPage) return;

    this.state.update((state) => ({
      ...state,
      currentPage: page,
    }));

    this.fetchTargetUnitFacilities().subscribe();
  }

  private fetchDetails() {
    return this.subsistenceFeesMoAInfoViewService.getSubsistenceFeesMoaDetailsById(this.moaId).pipe(
      tap((details) => {
        this.moaDetails = details;
        this.data = toTuMoaDetailsSummary(details);
      }),
      switchMap(() => this.fetchTargetUnitFacilities()),
    );
  }

  private fetchTargetUnitFacilities() {
    const searchCriteria: SubsistenceFeesSearchCriteria = {
      pageNumber: this.state().currentPage - 1,
      pageSize: this.state().pageSize,
      ...this.searchForm.value,
    };

    return this.subsistenceFeesMoATargetUnitInfoViewService
      .getSubsistenceFeesMoaFacilities(this.moaDetails.moaTargetUnitId, searchCriteria)
      .pipe(
        takeUntilDestroyed(this.destroyref),
        tap((results) => {
          this.state.update((state) => ({
            ...state,
            facilities: results.subsistenceFeesMoaFacilities,
            totalItems: results.total,
          }));
        }),
      );
  }
}
