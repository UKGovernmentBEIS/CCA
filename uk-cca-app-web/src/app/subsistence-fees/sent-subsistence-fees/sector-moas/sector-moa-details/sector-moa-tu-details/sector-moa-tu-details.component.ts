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
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { tap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { BreadcrumbService } from '@netz/common/navigation';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PaginationComponent, SummaryComponent, SummaryData, UtilityPanelComponent } from '@shared/components';
import { SubsistenceFeesRunMarkFacilitiesStatusPipe } from '@shared/pipes';

import {
  SubsistenceFeesMoaDetailsDTO,
  SubsistenceFeesMoaFacilitySearchResultInfoDTO,
  SubsistenceFeesMoATargetUnitInfoViewService,
  SubsistenceFeesRunDetailsDTO,
  SubsistenceFeesSearchCriteria,
} from 'cca-api';

import { toSectorMoaTUDetailsSummary } from './sector-moa-tu-details-summary';
import {
  TARGET_UNIT_FACILITIES_LIST_FORM,
  TargetUnitFacilitiesListFormModel,
  TargetUnitFacilitiesListFormProvider,
} from './tu-facilities-list-form.provider';

type TargetUnitFacilitiesListState = {
  facilities: SubsistenceFeesMoaFacilitySearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-sector-moa-tu-details',
  templateUrl: './sector-moa-tu-details.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
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
export class SectorMoaTuDetailsComponent implements OnInit, AfterViewChecked {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyref = inject(DestroyRef);
  private readonly subsistenceFeesMoATargetUnitInfoViewService = inject(SubsistenceFeesMoATargetUnitInfoViewService);
  private readonly breadcrumbService = inject(BreadcrumbService);
  private readonly markFacilitiesStatusPipe = inject(SubsistenceFeesRunMarkFacilitiesStatusPipe);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;
  readonly sectorMoaDetails = this.activatedRoute.snapshot.data.sectorMoaDetails as SubsistenceFeesMoaDetailsDTO;
  readonly moaTUId = +this.activatedRoute.snapshot.paramMap.get('moaTUId');
  readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  readonly moaTUDetails = toSignal(this.fetchMoaTUDetails());
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
    this.fetchTargetUnitFacilities().subscribe();
  }

  ngAfterViewChecked(): void {
    if (this.subFeesDetails) {
      this.showSubsistenceFeesFlowBreadcrumb();
    } else {
      this.showSectorSubsistenceFeesFlowBreadcrumb();
    }
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

  private fetchMoaTUDetails() {
    return this.subsistenceFeesMoATargetUnitInfoViewService
      .getSubsistenceFeesMoaTargetUnitDetailsById(this.moaTUId)
      .pipe(tap((res) => (this.data = toSectorMoaTUDetailsSummary(res))));
  }

  private fetchTargetUnitFacilities() {
    const searchCriteria: SubsistenceFeesSearchCriteria = {
      pageNumber: this.state().currentPage - 1,
      pageSize: this.state().pageSize,
      ...this.searchForm.value,
    };

    return this.subsistenceFeesMoATargetUnitInfoViewService
      .getSubsistenceFeesMoaFacilities(this.moaTUId, searchCriteria)
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

  private showSubsistenceFeesFlowBreadcrumb() {
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
        text: `${this.subFeesDetails.paymentRequestId}: Sector MoAs`,
        link: ['/', 'subsistence-fees', 'sent-subsistence-fees', `${this.subFeesDetails.runId}`],
        fragment: 'sector-moas',
      },
      {
        text: `${this.sectorMoaDetails.transactionId}`,
        link: [
          '/',
          'subsistence-fees',
          'sent-subsistence-fees',
          `${this.subFeesDetails.runId}`,
          'sector-moas',
          `${this.sectorMoaDetails.moaId}`,
          'details',
        ],
      },
    ]);
  }

  private showSectorSubsistenceFeesFlowBreadcrumb() {
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Manage sectors',
        link: ['/', 'sectors'],
      },
      {
        text: `${this.sectorMoaDetails.businessId} - ${this.sectorMoaDetails.name}`,
        link: ['/', 'sectors', `${this.sectorId}`],
        fragment: 'subsistence-fees',
      },
    ]);
  }
}
