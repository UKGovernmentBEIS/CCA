import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { TextareaComponent } from '@netz/govuk-components';
import {
  MultipleFileInputComponent,
  SummaryComponent,
  TextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { TerminatedTransactionComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AccountBuyOutSurplusTransactionDTO, BuyOutSurplusTransactionDetailUpdateControllerService } from 'cca-api';

import { CHANGE_AMOUNT_FORM, ChangeAmountFormModel, ChangeAmountFormProvider } from './change-amount-form.provider';
import { toChangeAmountSummaryData } from './change-amount-summary-data';

@Component({
  selector: 'cca-change-amount-component',
  templateUrl: './change-amount.component.html',
  providers: [ChangeAmountFormProvider],
  imports: [
    SummaryComponent,
    WizardStepComponent,
    TerminatedTransactionComponent,
    ReactiveFormsModule,
    TextInputComponent,
    MultipleFileInputComponent,
    TextareaComponent,
  ],
})
export class ChangeAmountComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutSurplusTransactionDetailUpdateControllerService = inject(
    BuyOutSurplusTransactionDetailUpdateControllerService,
  );

  protected readonly form = inject<ChangeAmountFormModel>(CHANGE_AMOUNT_FORM);

  protected readonly transactionDetails = this.activatedRoute.snapshot.data
    .transactionDetails as AccountBuyOutSurplusTransactionDTO;

  protected readonly summaryData = toChangeAmountSummaryData(this.transactionDetails);

  onSubmit() {
    this.buyOutSurplusTransactionDetailUpdateControllerService
      .updateBuyOutSurplusTransactionDetailAmount(this.transactionDetails.detailId, {
        amount: this.form.value.amount,
        comments: this.form.value.comments,
        evidenceFiles: fileUtils.toAttachments(this.form.value.evidenceFiles) ?? {},
      })
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
