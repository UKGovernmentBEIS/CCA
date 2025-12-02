import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { catchError, of, take } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CompaniesHouseDetailsComponent,
  CompaniesHouseState,
  toReviewTargetUnitDetailsSummaryData,
  transformAccountReferenceData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';

import { CompaniesInformationService, CompanyProfileDTO } from 'cca-api';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  templateUrl: './review-target-unit-details-summary.component.html',
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    CompaniesHouseDetailsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsSummaryComponent implements OnInit {
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

  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  );

  private readonly targetUnitDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly tuDetails = computed(() =>
    this.targetUnitDetails() ? this.targetUnitDetails() : transformAccountReferenceData(this.accountReferenceData()),
  );

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsSummaryData(
      this.targetUnitDetails(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.companiesHouseState(),
      this.toggleCompaniesHouseDetails(),
    ),
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
