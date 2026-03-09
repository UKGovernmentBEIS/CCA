import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { catchError, of, take } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CompaniesHouseDetailsComponent,
  CompaniesHouseState,
  toVariationReviewTargetUnitDetailsSummaryDataWithDecision,
  transformAccountReferenceData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';

import { CompaniesInformationService, CompanyProfileDTO } from 'cca-api';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  templateUrl: './review-target-unit-details-summary.component.html',
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
    ReactiveFormsModule,
    CompaniesHouseDetailsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsSummaryComponent implements OnInit {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly companiesInformationService = inject(CompaniesInformationService);

  protected readonly toggleCompaniesHouseDetailsCtrl = new FormControl<boolean>(false);
  protected readonly toggleCompaniesHouseDetails = toSignal(this.toggleCompaniesHouseDetailsCtrl.valueChanges, {
    initialValue: false,
  });

  protected readonly mainColumnClass = computed(() =>
    this.toggleCompaniesHouseDetails() ? 'govuk-grid-column-two-thirds' : 'govuk-grid-column-full',
  );

  private readonly companiesHouseDetailsResponse = signal<CompanyProfileDTO | null>(null);

  protected readonly companiesHouseState = computed<CompaniesHouseState>(() => {
    const response = this.companiesHouseDetailsResponse();
    return {
      details: typeof response === 'object' ? response : null,
      address: typeof response === 'object' ? transformAddress(response?.address).join('\n') : null,
    };
  });

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  )();
  private readonly originalTargetUnitDetails = transformAccountReferenceData(this.accountReferenceData);
  private readonly currentTargetUnitDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly tuDetails = computed(() =>
    this.currentTargetUnitDetails() ? this.currentTargetUnitDetails() : this.originalTargetUnitDetails,
  );

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
  )();

  protected readonly summaryDataOriginal = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.originalTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  protected readonly summaryDataCurrent = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.currentTargetUnitDetails(),
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  ngOnInit() {
    if (this.tuDetails().companyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.tuDetails().companyRegistrationNumber)
        .pipe(
          take(1),
          catchError(() => of(null)),
        )
        .subscribe((res) => {
          if (typeof res === 'object') {
            this.companiesHouseDetailsResponse.set(res);
          }
        });
    }
  }
}
