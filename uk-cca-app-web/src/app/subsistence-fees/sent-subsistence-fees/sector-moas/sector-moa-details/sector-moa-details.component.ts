import { DecimalPipe } from '@angular/common';
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
import { ActivatedRoute, RouterLink } from '@angular/router';

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
  TagComponent,
  TextInputComponent,
  WarningTextComponent,
} from '@netz/govuk-components';
import { PaginationComponent, SummaryComponent, UtilityPanelComponent } from '@shared/components';
import {
  SubsistenceFeesRunMarkFacilitiesStatusPipe,
  SubsistenceFeesRunPaymentStatusPipe,
  SubsistenceFeesRunPaymentStatusTagColorPipe,
} from '@shared/pipes';

import {
  SubsistenceFeesMoaDetailsDTO,
  SubsistenceFeesMoAInfoViewService,
  SubsistenceFeesMoaTargetUnitSearchResultInfoDTO,
  SubsistenceFeesRunDetailsDTO,
  SubsistenceFeesSearchCriteria,
} from 'cca-api';

import { toSectorMoaDetailsSummary } from './sector-moa-details-summary';
import { TARGET_UNITS_LIST_FORM, TargetUnitsListFormModel, TargetUnitsListFormProvider } from './tu-list-form.provider';

type TargetUnitsListState = {
  targetUnits: SubsistenceFeesMoaTargetUnitSearchResultInfoDTO[];
  currentPage: number;
  totalItems: number;
  pageSize: number;
};

@Component({
  selector: 'cca-sector-moa-details',
  templateUrl: './sector-moa-details.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    TableComponent,
    TagComponent,
    PaginationComponent,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    SelectComponent,
    PageHeadingComponent,
    SubsistenceFeesRunPaymentStatusPipe,
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
    DecimalPipe,
    SummaryComponent,
    WarningTextComponent,
    UtilityPanelComponent,
  ],
  providers: [
    TargetUnitsListFormProvider,
    SubsistenceFeesRunPaymentStatusPipe,
    SubsistenceFeesRunMarkFacilitiesStatusPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorMoaDetailsComponent implements OnInit, AfterViewChecked {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyref = inject(DestroyRef);
  private readonly subsistenceFeesMoAInfoViewService = inject(SubsistenceFeesMoAInfoViewService);
  private readonly breadcrumbService = inject(BreadcrumbService);
  private readonly markFacilitiesStatusPipe = inject(SubsistenceFeesRunMarkFacilitiesStatusPipe);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;
  readonly sectorMoaDetails = this.activatedRoute.snapshot.data.sectorMoaDetails as SubsistenceFeesMoaDetailsDTO;
  readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  readonly data = toSectorMoaDetailsSummary(this.sectorMoaDetails);
  readonly higherReceivedAmount = this.sectorMoaDetails.receivedAmount > this.sectorMoaDetails.currentTotalAmount;

  readonly state = signal<TargetUnitsListState>({
    targetUnits: [],
    currentPage: +this.activatedRoute.snapshot.paramMap.get('page') || 1,
    totalItems: 0,
    pageSize: 30,
  });

  readonly searchForm = inject<TargetUnitsListFormModel>(TARGET_UNITS_LIST_FORM);

  readonly markingOfFacilitiesOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'IN_PROGRESS', text: this.markFacilitiesStatusPipe.transform('IN_PROGRESS') },
    { value: 'COMPLETED', text: this.markFacilitiesStatusPipe.transform('COMPLETED') },
    { value: 'CANCELLED', text: this.markFacilitiesStatusPipe.transform('CANCELLED') },
  ];

  readonly tableColumns: GovukTableColumn[] = [
    { field: 'businessId', header: 'Target unit ID' },
    { field: 'name', header: 'Operator' },
    { field: 'markFacilitiesStatus', header: 'Marking of facilities' },
    { field: 'currentTotalAmount', header: 'Total (GBP)' },
  ];

  ngOnInit() {
    this.fetchTargetUnits().subscribe();
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
    this.fetchTargetUnits().subscribe();
  }

  onClearFilters() {
    this.searchForm.reset();
    this.fetchTargetUnits().subscribe();
  }

  handlePageChange(page: number) {
    if (page === this.state().currentPage) return;

    this.state.update((state) => ({
      ...state,
      currentPage: page,
    }));

    this.fetchTargetUnits().subscribe();
  }

  private fetchTargetUnits() {
    const searchCriteria: SubsistenceFeesSearchCriteria = {
      pageNumber: this.state().currentPage - 1,
      pageSize: this.state().pageSize,
      ...this.searchForm.value,
    };

    return this.subsistenceFeesMoAInfoViewService
      .getSubsistenceFeesMoaTargetUnits(this.sectorMoaDetails.moaId, searchCriteria)
      .pipe(
        takeUntilDestroyed(this.destroyref),
        tap((results) => {
          this.state.update((state) => ({
            ...state,
            targetUnits: results.subsistenceFeesMoaTargetUnits,
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
