import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';
import { SchemeVersion, SchemeVersionEnum } from '@shared/types';

import { BuyOutSurplusTransactionsListSearchCriteria } from 'cca-api';

import { TargetPeriodTypesBySchemeVersion } from '../utils';
import {
  toTransactionFiltersFormValue,
  TRANSACTION_REPORT_FORM,
  transactionInitialValues,
  TransactionReportFormModel,
  TransactionReportFormProvider,
} from './transactions-filters-form.provider';

// we need this type to match the expected query params. Our url query params uses `page` instead of `pageNumber`
type TransactionsCriteriaParams = Omit<BuyOutSurplusTransactionsListSearchCriteria, 'pageNumber'> & { page: number };

@Component({
  selector: 'cca-transaction-report-filters',
  templateUrl: './transactions-filters.component.html',
  providers: [TransactionReportFormProvider],
  imports: [ReactiveFormsModule, TextInputComponent, SelectComponent, ButtonDirective, UtilityPanelComponent],
})
export class TransactionsFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject<TransactionReportFormModel>(TRANSACTION_REPORT_FORM);

  protected readonly schemeVersionOptions: GovukSelectOption<SchemeVersion>[] = [
    { value: SchemeVersionEnum.CCA_2, text: SchemeVersionEnum.CCA_2 },
    { value: SchemeVersionEnum.CCA_3, text: SchemeVersionEnum.CCA_3 },
  ];

  private readonly schemeVersion = signal(this.filtersForm.controls.schemeVersion.value);

  protected readonly targetPeriodOptions = computed<GovukSelectOption[]>(() => [
    { value: null, text: 'All' },
    ...TargetPeriodTypesBySchemeVersion[this.schemeVersion()].map((targetPeriodType) => ({
      value: targetPeriodType,
      text: targetPeriodType,
    })),
  ]);

  protected readonly statusOptions: GovukSelectOption[] = [
    { value: null, text: 'All' },
    { value: 'AWAITING_PAYMENT', text: 'Awaiting payment' },
    { value: 'AWAITING_REFUND', text: 'Awaiting refund' },
    { value: 'PAID', text: 'Paid' },
    { value: 'REFUNDED', text: 'Refunded' },
    { value: 'NOT_REQUIRED', text: 'Not required' },
    { value: 'UNDER_APPEAL', text: 'Under appeal' },
    { value: 'TERMINATED', text: 'Terminated' },
  ];

  constructor() {
    // The URL is the source of truth: keep the form in sync when it changes without this page being recreated,
    // e.g. when the menu link or a tab switch resets the query params.
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((queryParamMap) => {
      const formValue = toTransactionFiltersFormValue(queryParamMap);
      this.schemeVersion.set(formValue.schemeVersion);
      this.filtersForm.patchValue(formValue, { emitEvent: false });
    });

    // The available periods depend on the selected scheme, so a scheme change falls back to "All"
    this.filtersForm.controls.schemeVersion.valueChanges.pipe(takeUntilDestroyed()).subscribe((schemeVersion) => {
      this.schemeVersion.set(schemeVersion);
      this.filtersForm.controls.targetPeriodType.setValue(null);
    });
  }

  clear() {
    this.filtersForm.reset(transactionInitialValues);
    this.handleQueryParamsNavigation({ ...transactionInitialValues, page: 1 });
  }

  apply() {
    const values = this.filtersForm.value;
    this.handleQueryParamsNavigation({ ...values, page: 1 });
  }

  private handleQueryParamsNavigation(criteria: Partial<TransactionsCriteriaParams>) {
    this.router.navigate([], {
      queryParams: { ...criteria },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
