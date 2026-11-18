import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
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

import { BuyOutSurplusTransactionDetailsDTO, BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import { CHANGE_AMOUNT_FORM, ChangeAmountFormModel, ChangeAmountFormProvider } from './change-amount-form.provider';
import { toChangeAmountSummaryData } from './change-amount-summary-data';

@Component({
  selector: 'cca-change-amount-component',
  templateUrl: './change-amount.component.html',
  imports: [
    SummaryComponent,
    WizardStepComponent,
    TerminatedTransactionComponent,
    ReactiveFormsModule,
    TextInputComponent,
    MultipleFileInputComponent,
    TextareaComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ChangeAmountFormProvider],
})
export class ChangeAmountComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutSurplusTransactionUpdateControllerService = inject(
    BuyOutSurplusTransactionUpdateControllerService,
  );

  private readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');

  protected readonly form = inject<ChangeAmountFormModel>(CHANGE_AMOUNT_FORM);

  protected readonly transactionDetails = this.activatedRoute.snapshot.data
    .transactionDetails as BuyOutSurplusTransactionDetailsDTO;

  protected readonly summaryData = toChangeAmountSummaryData(this.transactionDetails);

  onSubmit() {
    this.buyOutSurplusTransactionUpdateControllerService
      .updateBuyOutSurplusTransactionAmount(this.transactionId, {
        amount: this.form.value.amount,
        comments: this.form.value.comments,
        evidenceFiles: fileUtils.toAttachments(this.form.value.evidenceFiles) ?? {},
      })
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
