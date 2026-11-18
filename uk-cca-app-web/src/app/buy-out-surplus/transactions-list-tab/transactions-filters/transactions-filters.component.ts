import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';

import { TransactionsCriteria } from '../utils';
import {
  TRANSACTION_REPORT_FORM,
  transactionInitialValues,
  TransactionReportFormModel,
  TransactionReportFormProvider,
} from './transactions-filters-form.provider';

// we need this type to match the expected query params. Our url query params uses `page` instead of `pageNumber`
type TransactionsCriteriaParams = Omit<TransactionsCriteria, 'pageNumber'> & { page: number };

@Component({
  selector: 'cca-transaction-report-filters',
  templateUrl: './transactions-filters.component.html',
  imports: [ReactiveFormsModule, TextInputComponent, SelectComponent, ButtonDirective, UtilityPanelComponent],
  providers: [TransactionReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionsFiltersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly filtersForm = inject<TransactionReportFormModel>(TRANSACTION_REPORT_FORM);

  protected readonly targetPeriodOptions: GovukSelectOption[] = [{ value: 'TP6', text: 'TP6' }];

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
