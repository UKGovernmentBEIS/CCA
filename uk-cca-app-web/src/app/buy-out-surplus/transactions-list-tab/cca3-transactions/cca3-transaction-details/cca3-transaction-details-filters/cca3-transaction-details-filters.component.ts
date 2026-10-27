import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { FacilityBuyOutSurplusTransactionDetailsSearchCriteria } from 'cca-api';

import {
  CCA3_TRANSACTION_DETAILS_FILTERS_FORM,
  CCA3TransactionDetailsFiltersFormModel,
  CCA3TransactionDetailsFiltersFormProvider,
  transactionInitialValues,
} from './cca3-transaction-details-filters-form.provider';

type Cca3TransactionDetailsCriteriaParams = Omit<
  FacilityBuyOutSurplusTransactionDetailsSearchCriteria,
  'pageNumber'
> & { page: number };

@Component({
  selector: 'cca-cca3-transaction-details-filters',
  template: `
    <cca-utility-panel heading="Filters">
      <form [formGroup]="filtersForm" (ngSubmit)="onApplyFilters()">
        <div class="govuk-grid-row">
          <div class="govuk-grid-column-one-half">
            <div
              widthClass="govuk-!-width-full"
              formControlName="term"
              govuk-text-input
              label="Facility ID or site name"
            ></div>
          </div>

          <div class="govuk-grid-column-one-half">
            <div
              govuk-select
              widthClass="govuk-!-width-full"
              [options]="paymentStatusOptions"
              label="Status"
              formControlName="paymentStatus"
            ></div>
          </div>
        </div>

        <div class="govuk-grid-row">
          <div class="govuk-grid-column-one-quarter">
            <button class="govuk-!-margin-right-3" netzPendingButton govukButton type="submit">Apply</button>
            <button netzPendingButton govukSecondaryButton type="button" (click)="onClearFilters()">Clear</button>
          </div>
        </div>
      </form>
    </cca-utility-panel>
  `,
  providers: [CCA3TransactionDetailsFiltersFormProvider, StatusPipe],
  imports: [
    ReactiveFormsModule,
    UtilityPanelComponent,
    ButtonDirective,
    PendingButtonDirective,
    TextInputComponent,
    SelectComponent,
  ],
})
export class Cca3TransactionDetailsFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly statusPipe = inject(StatusPipe);

  protected readonly filtersForm = inject<CCA3TransactionDetailsFiltersFormModel>(
    CCA3_TRANSACTION_DETAILS_FILTERS_FORM,
  );

  protected readonly paymentStatusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'AWAITING_PAYMENT', text: this.statusPipe.transform('AWAITING_PAYMENT') },
    { value: 'AWAITING_REFUND', text: this.statusPipe.transform('AWAITING_REFUND') },
    { value: 'UNDER_APPEAL', text: this.statusPipe.transform('UNDER_APPEAL') },
    { value: 'PAID', text: this.statusPipe.transform('PAID') },
    { value: 'REFUNDED', text: this.statusPipe.transform('REFUNDED') },
    { value: 'NOT_REQUIRED', text: this.statusPipe.transform('NOT_REQUIRED') },
    { value: 'TERMINATED', text: this.statusPipe.transform('TERMINATED') },
  ];

  onApplyFilters() {
    if (this.filtersForm.invalid) return;
    this.handleQueryParamsNavigation({ ...this.filtersForm.value, page: 1 });
  }

  onClearFilters() {
    this.filtersForm.reset(transactionInitialValues);
    this.handleQueryParamsNavigation({ ...transactionInitialValues, page: 1 });
  }

  private handleQueryParamsNavigation(criteria: Partial<Cca3TransactionDetailsCriteriaParams>) {
    this.router.navigate([], {
      queryParams: { ...criteria },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
    });
  }
}
